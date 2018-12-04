package com.beheresoft.download.component.hanlder

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class FileHanlder : ChannelInboundHandlerAdapter {

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
    }
}