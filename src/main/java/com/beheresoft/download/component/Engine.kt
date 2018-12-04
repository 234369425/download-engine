package com.beheresoft.download.component

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

class Engine {

    fun create(name: String, nThreads: Int) {
        val group = NioEventLoopGroup(nThreads)
        val bootStrap = Bootstrap()
        bootStrap.group(group)
                .channel(NioSocketChannel::class.java)
    }

}