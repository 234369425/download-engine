package com.beheresoft.download.component.download.http.entity

data class FileDescrib(
        val name: String,
        val pos: Long,
        val md5: String?,
        val type: String?,
        val size: Long
) {
    var blocks: List<Block> = ArrayList()
}