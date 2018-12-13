package com.beheresoft.download.component.download.http.handler

import com.beheresoft.download.component.download.http.callback.Callback
import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.component.download.http.utils.SSL
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.timeout.ReadTimeoutHandler

class DownloadHandler(private val block: Block,
                      private val callback: Callback? = null) : ChannelInitializer<Channel>() {

    override fun initChannel(ch: Channel) {
        val request = block.request
        val pipeline = ch.pipeline()
        if (request.ssl) {
            pipeline.addLast(SSL.handler(ch, request.host!!, request.port))
        }
        pipeline.addLast("timeout", ReadTimeoutHandler(3 * 60))
        pipeline.addLast("httpCodec", HttpClientCodec())
        pipeline.addLast(InboundHandlerAdapter(block, callback))
    }

}