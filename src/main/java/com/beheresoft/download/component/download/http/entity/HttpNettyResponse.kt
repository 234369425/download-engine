package com.beheresoft.download.component.download.http.entity

data class HttpNettyResponse(val fileName: String, val size: Long, val supportBlock: Boolean = false) {

}