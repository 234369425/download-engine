package com.beheresoft.download.component.download.http.handler

import com.beheresoft.download.component.download.http.Task
import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.component.download.http.entity.Request
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandler
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.LastHttpContent
import java.net.URL
import java.nio.channels.SeekableByteChannel
import java.util.logging.Logger

class InboundHandlerAdapter constructor(private val task: Task, private val block: Block,
                                        private val request: Request) : ChannelInboundHandler {

    private var normalClose: Boolean = true
    private lateinit var fileChannel: SeekableByteChannel
    private var success: Boolean = false
    private val log = Logger.getLogger(InboundHandlerAdapter::class.simpleName)
    private val ssl = Regex("(?i)(https?).*")

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        when (msg) {
            is HttpContent -> {
                if (!success || !fileChannel.isOpen || ctx == null || ctx.channel().isOpen) {
                    return
                }
                val content: HttpContent = msg
                val buff = content.content()
                var size = buff.readableBytes()

                if (task.supportBlock && block.downSize + size > block.size()) {
                    size = (block.size() - block.downSize).toInt()
                }

                fileChannel.write(buff.nioBuffer())
                synchronized(block) {
                    block.plusDownSize(size)
                    task.addDownSize(size)

                }
                if (!task.supportBlock && content !is LastHttpContent) {
                    return
                }
            }
            else -> checkResponse(msg as HttpResponse)
        }
    }

    private fun checkResponse(response: HttpResponse) {
        val httpCode = response.status().code()
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
            in 300..400 -> {
                log.warning("http response code $httpCode ")
                block.plusErrorTimes()
                normalClose = true

            }
        }
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelRegistered(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlerAdded(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}