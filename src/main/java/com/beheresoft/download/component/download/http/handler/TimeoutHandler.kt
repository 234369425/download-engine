package com.beheresoft.download.component.download.http.handler

import io.netty.handler.timeout.ReadTimeoutHandler

class TimeoutHandler constructor(private val seconds: Int) : ReadTimeoutHandler(seconds) {

}