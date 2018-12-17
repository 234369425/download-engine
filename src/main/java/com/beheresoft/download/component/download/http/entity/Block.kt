package com.beheresoft.download.component.download.http.entity

import com.beheresoft.download.component.download.http.handler.DownloadHandler
import com.beheresoft.download.enums.DownLoadStatus
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpHeaderNames
import org.slf4j.LoggerFactory
import java.nio.channels.SeekableByteChannel

data class Block(var start: Long, var end: Long,
                 var request: Request, var loopGroup: NioEventLoopGroup,
                 var fileChannel: SeekableByteChannel) {

    var connect: Channel? = null
    var downSize: Long = 0
    var status: DownLoadStatus = DownLoadStatus.WAIT
    var size: Long = end - start
    private var errorTimes: Int = 0
    private var position: Long = 0
    private var lastTotalTime = System.currentTimeMillis()
    private val log = LoggerFactory.getLogger(Block::class.java)
    private var bootstrap: Bootstrap? = null

    fun supportSegmentation() = end > 0

    fun isDone() = supportSegmentation() && downSize >= size

    fun start() {
        log.debug("block start $start end $end")
        if (bootstrap == null) {
            bootstrap = Bootstrap()
            bootstrap!!.channel(NioSocketChannel::class.java)
                    .group(loopGroup)
                    .handler(DownloadHandler(this))
            val chFu = bootstrap!!.connect(request.host, request.port)
            connect = bootstrap?.connect(request.host, request.port)?.channel()
            chFu.addListener {
                if (it.isSuccess) {
                    log.debug("connect success begin download ")
                    status = DownLoadStatus.DOWNING
                    if (supportSegmentation()) {
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

        if (status != DownLoadStatus.DONE) {
            status = DownLoadStatus.DOWNING
        }

    }

    fun pause() {

    }

    fun getSpeed(): Int {
        if (lastTotalTime == 0L) {
            lastTotalTime = System.currentTimeMillis()
            return 0
        }
        val denominator = System.currentTimeMillis() - lastTotalTime
        if (denominator == 0L) {
            return 0
        }
        val speed = (downSize - position) / denominator / 60
        position = downSize
        lastTotalTime = System.currentTimeMillis()
        return speed.toInt()
    }

    fun resetErrorTime() {
        errorTimes = 0
    }

    fun plusDownSize(size: Int) {
        downSize = downSize.plus(size)
    }

    fun plusErrorTimes() {
        errorTimes = errorTimes.plus(1)
    }

}