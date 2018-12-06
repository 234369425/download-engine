package com.beheresoft.download.component.download.http.entity

import io.netty.handler.codec.DecoderResult
import io.netty.handler.codec.http.*
import java.io.Serializable
import java.net.URL

class Request(private var url: String) : HttpRequest, Serializable {

    lateinit var host: String
    var port: Int = 0
    var ssl: Boolean = false
    var httpMethod: HttpMethod? = HttpMethod.GET
    var version: HttpVersion? = HttpVersion.HTTP_1_1
    var header: HttpHeader
    var content: DefaultLastHttpContent? = null

    init {
        val u = URL(url)
        header = DefaultHttpHeader.get(u.host)
    }

    fun setURL(url: String) {
        this.url = url
        val u = URL(url)
        port = if (u.port == -1) u.defaultPort else u.port
        ssl = "https".equals(u.protocol, true)
    }

    fun body(body: String) {
        content = DefaultLastHttpContent()
        val data = body.toByteArray()
        content?.content()?.writeBytes(data)
        header.add(HttpHeaderNames.CONTENT_LENGTH, data.size)
    }

    fun hasBody() = content != null

    override fun uri(): String {
        return url
    }

    override fun protocolVersion(): HttpVersion {
        return version ?: HttpVersion.HTTP_1_1
    }

    override fun setUri(uri: String): HttpRequest {
        setURL(uri)
        return this
    }

    override fun getMethod(): HttpMethod {
        return method
    }

    override fun getProtocolVersion(): HttpVersion {
        return version ?: HttpVersion.HTTP_1_1
    }

    override fun method(): HttpMethod {
        return httpMethod ?: HttpMethod.GET
    }

    override fun getUri(): String {
        return uri()
    }

    override fun setMethod(method: HttpMethod?): HttpRequest {
        httpMethod = method
        return this
    }

    override fun decoderResult(): DecoderResult {
        return DecoderResult.SUCCESS
    }

    override fun getDecoderResult(): DecoderResult {
        return DecoderResult.SUCCESS
    }

    override fun setDecoderResult(result: DecoderResult?) {

    }

    override fun headers(): HttpHeaders {
        return headers()
    }

    override fun setProtocolVersion(version: HttpVersion?): HttpRequest {
        this.version = version ?: HttpVersion.HTTP_1_1
        return this
    }
}