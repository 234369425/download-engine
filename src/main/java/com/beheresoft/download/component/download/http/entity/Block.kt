package com.beheresoft.download.component.download.http.entity

import com.beheresoft.download.component.download.http.HttpDownloadEvent
import com.beheresoft.download.enums.DownLoadStatus
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpHeaderNames
import org.slf4j.LoggerFactory
import java.nio.channels.SeekableByteChannel

data class Block(var start: Long, var end: Long, var downSize: Long,
                 override var request: Request, override var loopGroup: NioEventLoopGroup) : HttpDownloadEvent {

    var errorTimes: Int = 0
    var connect: Channel? = null
    var fileChannel: SeekableByteChannel? = null
    var status: DownLoadStatus = DownLoadStatus.WAIT
    private var position: Long = 0
    private var lastTotalTime = System.currentTimeMillis()
    private val log = LoggerFactory.getLogger(Block::class.java)
    private var bootstrap: Bootstrap? = null

    private fun supportBlock() = end > 0

    override fun start() {
        log.info("start block start $start end $end")
        if (bootstrap == null) {
            bootstrap = Bootstrap()
            bootstrap!!.channel(NioSocketChannel::class.java)
                    .group(loopGroup)
                    .handler()
            val chFu = bootstrap!!.connect(request.host, request.port)
            connect = bootstrap?.connect(request.host, request.port)?.channel()
            chFu.addListener {
                val itFu = it as ChannelFutureListener
                if (it.isSuccess) {
                    log.debug("connect success begin download ")
                    status = DownLoadStatus.DOWNING
                    if (supportBlock()) {
                        request.headers().set(HttpHeaderNames.RANGE, "bytes=$start-$end")
                    } else {
                        request.headers().remove(HttpHeaderNames.RANGE)
                    }
                    connect?.writeAndFlush(request)
                    if (request.hasBody()) {
                        connect?.writeAndFlush(request.content)
                    }
                } else {
                    connect?.close()
                }
            }
        }

    }

    override fun remove() {

    }

    override fun pause() {

    }

    fun getSpeed(): Int {
        val speed = (downSize - position) / (System.currentTimeMillis() - lastTotalTime) / 60
        position = downSize
        lastTotalTime = System.currentTimeMillis()
        return speed.toInt()
    }

    fun resetErrorTime() {
        errorTimes = 0
    }

    fun size() = end + 1 - start

    fun plusDownSize(size: Int) {
        downSize = downSize.plus(size)
    }

    fun plusErrorTimes() {
        errorTimes = errorTimes.plus(1)
    }

    class BlockDown : ChannelInitializer<Channel>() {
        override fun initChannel(ch: Channel) {

        }

    }
}