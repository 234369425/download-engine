package com.beheresoft.download.component.download.http.utils

import io.netty.channel.Channel

object SafeClose {

    fun close(channel: Channel?) {
        channel ?: return
        if (channel.isOpen) {
            channel.close()
        }
    }

    fun close(closeable: AutoCloseable?) {
        closeable ?: return
        closeable.close()
    }

}