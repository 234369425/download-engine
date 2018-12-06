package com.beheresoft.download.component.download.http.entity

import com.beheresoft.download.component.download.http.enums.UserAgents
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues

class DefaultHttpHeader {

    companion object {
        private val template = HttpHeader()
        private val userAgent = HashMap<UserAgents, String>()

        init {
            userAgent[UserAgents.MAC_OS] = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9"
            userAgent[UserAgents.WIN_CHROME] = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36"

            template.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                    .add(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
        }

        fun get(host: String, ua: UserAgents = UserAgents.MAC_OS): HttpHeader {
            val copy = template.copy()
            copy.add(HttpHeaderNames.HOST, host)
                    .add(HttpHeaderNames.REFERER, host)
                    .add(HttpHeaderNames.USER_AGENT, userAgent[ua])
            return copy
        }


    }

}