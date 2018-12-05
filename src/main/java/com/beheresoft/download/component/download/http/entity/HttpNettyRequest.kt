package com.beheresoft.download.component.download.http.entity

import io.netty.handler.codec.DecoderResult
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpVersion
import java.io.Serializable

class HttpNettyRequest : HttpRequest, Serializable {

    lateinit var host: String
    var port: Int = 0
    var ssl: Boolean = false
    var url: String? = null
    var httpMethod: HttpMethod? = HttpMethod.GET
    var version: HttpVersion? = HttpVersion.HTTP_1_1
    lateinit var header: HttpHeader
    lateinit var content: ByteArray

    override fun uri(): String {
        return url ?: ""
    }

    override fun protocolVersion(): HttpVersion {
        return version ?: HttpVersion.HTTP_1_1
    }

    override fun setUri(uri: String?): HttpRequest {
        url = uri
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