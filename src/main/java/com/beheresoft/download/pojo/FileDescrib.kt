package com.beheresoft.download.pojo

data class FileDescrib(
        val name: String,
        val pos: Long,
        val md5: String?,
        val type: String?,
        val size: Long

)