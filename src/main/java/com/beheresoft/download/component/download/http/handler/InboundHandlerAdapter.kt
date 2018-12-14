package com.beheresoft.download.component.download.http.handler

import com.beheresoft.download.component.download.http.callback.Callback
import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.enums.DownLoadStatus
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandler
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.LastHttpContent
import org.slf4j.LoggerFactory
import java.net.URL

class InboundHandlerAdapter constructor(private val block: Block,
                                        private val callback: Callback?) : ChannelInboundHandler {

    private var normalClose: Boolean = true
    private val log = LoggerFactory.getLogger(InboundHandlerAdapter::class.java.name)
    private val ssl = Regex("(?i)(https?).*")

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any?) {
        ctx.fireUserEventTriggered(evt)
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        ctx.fireChannelWritabilityChanged()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        when (msg) {
            is HttpContent -> {
                if (!block.fileChannel.isOpen || !ctx.channel().isOpen) {
                    return
                }
                val content: HttpContent = msg
                val buff = content.content()
                var size = buff.readableBytes()
                //檢查是否超出範圍
                if (block.size() > 0 && block.taskSize + size > block.size()) {
                    size = (block.size() - block.taskSize).toInt()
                }

                block.fileChannel.write(buff.nioBuffer())
                synchronized(block) {
                    block.plusDownSize(size)
                }

                if (block.supportSegmentation() && content !is LastHttpContent) {
                    return
                }

                if (!isDone(content)) {
                    return
                }
                callback?.onBlockDone(block)
                block.status = DownLoadStatus.DONE
                //SafeClose.close(block.connect)
            }
            else -> checkResponse(msg as HttpResponse)
        }
    }


    private fun isDone(content: HttpContent): Boolean {
        if (block.supportSegmentation()) {
            return block.isDone()
        }
        return content is LastHttpContent
    }

    private fun checkResponse(response: HttpResponse) {
        val httpCode = response.status().code()
        val request = block.request
        when (httpCode) {
            //重定向
            in 300..399 -> {
                val location = response.headers().get(HttpHeaderNames.LOCATION)
                if (location.matches(ssl)) {
                    val url = URL(location)
                    request.uri = url.file
                    request.header[HttpHeaderNames.HOST] = url.host
                    request.port = if (url.port == -1) url.defaultPort else url.port
                    request.ssl = url.protocol.equals("https", true)
                } else {
                    request.setUri(location)
                }
            }
            in 0..199,
            in 300..399 -> {
                log.warn("http response code $httpCode ")
                block.plusErrorTimes()
                normalClose = true

            }
        }
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        ctx.fireChannelUnregistered()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
    }

    override fun channelRegistered(ctx: ChannelHandlerContext) {
        ctx.fireChannelRegistered()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.fireChannelReadComplete()
    }

    override fun handlerAdded(ctx: ChannelHandlerContext?) {

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {

    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.fireChannelInactive()
    }

}