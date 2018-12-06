package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.http.entity.Request
import com.beheresoft.download.component.download.http.exception.HttpDownloadBootstrapException
import com.beheresoft.download.component.download.http.exception.TaskCreateErrorException
import com.beheresoft.download.config.DownloadConfig
import com.beheresoft.download.entity.Task
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class HttpDownloadBootStrap constructor(url: String, private val config: DownloadConfig) {

    private var request = Request(url)
    var loopGroup: NioEventLoopGroup? = null
    private lateinit var task: Task

    fun addBody(body: String) {
        request.body(body)
    }

    fun start() {
        task = Task()
        if (loopGroup == null) {
            loopGroup = NioEventLoopGroup(1)
        }


    }

    private fun fillTask() {
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
                fillTask()
            }
        }
        if (code != HttpResponseStatus.OK.code() && code != HttpResponseStatus.PARTIAL_CONTENT.code()) {
            throw HttpDownloadBootstrapException()
        }
        return responseToTask(response)
    }

    private fun responseToTask(response: HttpResponse) {

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