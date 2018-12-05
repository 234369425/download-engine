package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.component.download.http.entity.HttpNettyRequest
import com.beheresoft.download.component.download.http.handler.InboundHandlerAdapter
import com.beheresoft.download.component.download.http.handler.TimeoutHandler
import io.netty.channel.Channel
import io.netty.channel.ChannelInboundHandler
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

class HttpDownInitializer(val request: HttpNettyRequest, val block: Block, var ssl: Boolean) : ChannelInitializer<Channel>() {

    override fun initChannel(ch: Channel?) {
        if (ssl) {
            ch?.pipeline()?.addLast(sslContent(ch))
        }
        ch?.pipeline()?.addLast("timeout", TimeoutHandler(100))
        ch?.pipeline()?.addLast("codec", HttpClientCodec())
        ch?.pipeline()?.addLast(InboundHandlerAdapter())
    }

    private fun sslContent(ch: Channel) = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
            .newHandler(ch.alloc(), request.host, request.port)

}