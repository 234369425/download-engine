package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.DownloadEvent
import com.beheresoft.download.component.download.http.entity.Request
import io.netty.channel.nio.NioEventLoopGroup

interface HttpDownloadEvent : DownloadEvent {
    var request: Request
    var loopGroup: NioEventLoopGroup
}