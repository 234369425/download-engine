package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.component.download.http.entity.Request
import com.beheresoft.download.component.download.http.exception.HttpDownloadBootstrapException
import com.beheresoft.download.component.download.http.exception.TaskCreateErrorException
import com.beheresoft.download.config.DownloadConfig
import com.beheresoft.download.utils.FileOperate
import com.beheresoft.download.utils.OS
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.regex.Pattern

class HttpDownloadBootStrap constructor(url: String, private val config: DownloadConfig) {

    private var request = Request(url)
    private val log = LoggerFactory.getLogger(HttpDownloadBootStrap::class.java)
    private var loopGroup: NioEventLoopGroup? = null
    private var task: Task
    private val filenamePattern = Pattern.compile("^.*filename\\*?=\"?(?:.*'')?([^\"]*)\"?$")

    init {
        if (loopGroup == null) {
            loopGroup = NioEventLoopGroup(1)
        }
        task = analysisTask()
        if (config.savePath.isEmpty()) {
            throw IOException("不知道要在哪保存")
        }
        val paths = Paths.get(config.savePath)
        if (!Files.isWritable(paths)) {
            throw IOException("无保存目录写权限")
        }
        if (!Files.exists(paths)) {
            Files.createDirectories(paths)
        }
        if (paths.toFile().freeSpace < task.size) {
            throw IOException("磁盘剩余空间不足")
        }
        val file = Paths.get(config.savePath + "/" + task.fileName).toFile()
        if (file.exists()) {
            Paths.get(paths.toUri().toString() + "/temp" + file.extension)
        }
        if (task.supportBlock) {
            val system = Files.getFileStore(paths).type().toUpperCase()
            if (OS.unix() || system == "NTFS" || system == "UFS" || system == "APFS") {
                FileOperate.createSparse(file.path, task.size)
            } else {
                FileOperate.createDefault(file.path, task.size)
            }
        }
        createBlocks()
        start()
    }

    private fun createBlocks() {
        if (task.size <= 0 || !task.supportBlock) {
            task.addBlock(Block(0, task.size - 1, task.size))
            return
        }
        val mbs10 = 1024 * 1024 * 10
        val connections = if (task.size < config.connections * mbs10) task.size.toInt() / mbs10 else config.connections
        val blockSize = task.size / connections
        for (i in 0..connections) {
            var start: Long = i * blockSize
            var end: Long = start + blockSize - 1
            var size: Long = blockSize
            if (i == connections - 1) {
                size += task.size % connections
                end += task.size % connections
            }
            task.addBlock(Block(start, end, size))
        }
    }


    fun addBody(body: String) {
        request.body(body)
    }

    fun start() {
        task.start()

    }

    private fun analysisTask(): Task {
        val response = checkResource() ?: throw TaskCreateErrorException()
        val code = response.status().code()
        when (code) {
            //重定向处理
            in 300..399 -> {
                val redirectUrl = response.headers().get(HttpHeaderNames.LOCATION)
                val header = request.header
                val cookies = header.getAll(HttpHeaderNames.SET_COOKIE)
                val oldCookies = header.get(HttpHeaderNames.COOKIE)
                val split = HttpConstants.SEMICOLON.toChar().toString() + HttpConstants.SP_CHAR.toString()
                val builder = StringBuilder(oldCookies).append(split)
                cookies.forEach {
                    it.split(split).forEach { n ->
                        builder.append(n).append(split)
                    }
                }
                header.set(HttpHeaderNames.COOKIE, builder)
                header.remove(HttpHeaderNames.HOST)
                request.setURL(redirectUrl)
                analysisTask()
            }
        }
        if (code != HttpResponseStatus.OK.code() && code != HttpResponseStatus.PARTIAL_CONTENT.code()) {
            throw HttpDownloadBootstrapException(code)
        }
        val task = Task()

        val resHeader = response.headers()
        val range = resHeader.get(HttpHeaderNames.CONTENT_RANGE)
        if (range == null) {
            val length = resHeader.get(HttpHeaderNames.CONTENT_LENGTH)
            task.size = length?.toLong() ?: -1
        } else {
            task.size = range.split("/").last().toLong()
        }
        task.supportBlock = resHeader.contains(HttpHeaderNames.CONTENT_LENGTH) && response.status() == HttpResponseStatus.PARTIAL_CONTENT
        val name = FileOperate.encodeName(resHeader.get(HttpHeaderNames.CONTENT_DISPOSITION))
        val matcher = filenamePattern.matcher(name)
        task.fileName = if (matcher.find()) matcher.group(1) else "unknown"
        return task
    }


    private fun checkResource(): HttpResponse? {
        var httpResponse: HttpResponse? = null
        val bootstrap = Bootstrap()
        val countDownLaunch = CountDownLatch(1)
        bootstrap.group(loopGroup)
                //使用NioSocket作为连接channel
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel) {
                        if (request.ssl) {
                            ch.pipeline().addLast(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
                                    .newHandler(ch.alloc(), request.host, request.port))
                        }
                        ch.pipeline().addLast("httpCodec", HttpClientCodec())
                        ch.pipeline().addLast(object : ChannelInboundHandlerAdapter() {
                            override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
                                if (msg is HttpResponse) {
                                    httpResponse = msg
                                    ctx.channel().close()
                                    countDownLaunch.countDown()
                                }
                            }
                        })
                    }
                })

        val cf = bootstrap.connect(request.host, request.port)
        cf.addListener {
            if (it.isSuccess) {
                log.debug(it.toString())
                //测试是否支持断点续传
                request.header.add(HttpHeaderNames.RANGE, "bytes=0-0")
                cf.channel().writeAndFlush(request)
                if (request.hasBody()) {
                    cf.channel().writeAndFlush(request.content)
                }
            } else {
                countDownLaunch.countDown()
            }
        }
        countDownLaunch.await(config.timeout, TimeUnit.SECONDS)
        if (httpResponse == null) {
            throw TimeoutException("connection time out ")
        }
        request.header.remove(HttpHeaderNames.RANGE)
        return httpResponse
    }

}