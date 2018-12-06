package com.beheresoft.download.component.download.http.entity

import com.beheresoft.download.enums.DownLoadStatus
import io.netty.channel.Channel
import java.nio.channels.SeekableByteChannel

data class Block(var start: Long, var end: Long, var downSize: Long, var connect: Channel, var fileChannel: SeekableByteChannel) {

    var errorTimes: Int = 0
    var status: DownLoadStatus = DownLoadStatus.WAIT
    var speed: Long = 0

    fun size() = end + 1 - start

    fun plusDownSize(size: Int) {
        downSize = downSize.plus(size)
    }

    fun plusErrorTimes() {
        errorTimes = errorTimes.plus(1)
    }
}