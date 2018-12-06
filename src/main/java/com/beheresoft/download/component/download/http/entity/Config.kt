package com.beheresoft.download.component.download.http.entity

data class Config(val filePath: String) {
    var connections: Int = 3
    var timeout: Int = 60
    var retryCount: Int = 5
    var speedLimit: Long = -1
}