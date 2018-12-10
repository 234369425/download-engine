package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.http.entity.Request
import com.beheresoft.download.component.download.http.exception.HttpDownloadBootstrapException
import com.beheresoft.download.component.download.http.exception.TaskCreateErrorException
import com.beheresoft.download.config.DownloadConfig
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
import java.net.URLDecoder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.regex.Pattern

class HttpDownloadBootStrap constructor(url: String, private val config: DownloadConfig) {

    private var request = Request(url)
    private val log = LoggerFactory.getLogger(HttpDownloadBootStrap::class.java)
    var loopGroup: NioEventLoopGroup? = null
    private lateinit var task: Task

    fun addBody(body: String) {
        request.body(body)
    }

    fun start() {
        if (loopGroup == null) {
            loopGroup = NioEventLoopGroup(1)
        }
        val task = analysisTask()
        print("")
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
        val name = encode(resHeader.get(HttpHeaderNames.CONTENT_DISPOSITION))
        val matcher = fileName.matcher(name)
        task.fileName = if (matcher.find()) matcher.group(1) else name
        return task
    }

    val fileName = Pattern.compile("^.*filename\\*?=\"?(?:.*'')?([^\"]*)\"?$")
    private fun encode(nettyString: String?): String {
        if (nettyString == null) return ""
        val encodeResult = StringBuilder()
        for (i in 0 until nettyString.length) {
            val ch = nettyString[i]
            if (ch in 'A'..'Z' || ch in 'a'..'z' ||
                    ch in '0'..'9' || ch == '-' ||
                    ch == '_' || ch == '.' || ch == '~' || ch == '/' ||
                    ch == '?' || ch == '=' || ch == '%' || ch == '&') {
                encodeResult.append(ch)
            } else {
                val index = ch.toInt() % 256
                encodeResult.append("%")
                encodeResult.append(urlHexTable[index])
            }
        }
        return URLDecoder.decode(encodeResult.toString(), "UTF-8")
    }

    val urlHexTable = arrayListOf(
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
            "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF",
            "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
            "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF",
            "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
            "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF",
            "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
    )

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