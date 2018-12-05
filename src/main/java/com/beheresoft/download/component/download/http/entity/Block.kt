package com.beheresoft.download.component.download.http.entity

import io.netty.channel.Channel
import java.nio.channels.SeekableByteChannel

data class Block(var start: Long, var end: Long, var download: Long, var connect: Channel, var channel: SeekableByteChannel) {

    var errorTimes: Int = 0

    fun size() = end + 1 - start

    fun plusDownSize(size: Int) {
        download = download.plus(size)
    }

    fun plusErrorTimes() {
        errorTimes = errorTimes.plus(1)
    }
}