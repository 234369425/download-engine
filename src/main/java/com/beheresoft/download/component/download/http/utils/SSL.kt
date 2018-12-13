package com.beheresoft.download.component.download.http.utils

import io.netty.channel.Channel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

object SSL {

    fun handler(ch: Channel, host: String, port: Int) = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
            .newHandler(ch.alloc(), host, port)

}