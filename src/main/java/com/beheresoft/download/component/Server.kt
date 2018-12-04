package com.beheresoft.download.component

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.util.logging.FileHandler

class Server{
    fun start(){
        val bossGroup = NioEventLoopGroup(1)
        val workGroup = NioEventLoopGroup()
        val b = ServerBootstrap()
        b.group(bossGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG,100)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(FileHandler())


    }
}