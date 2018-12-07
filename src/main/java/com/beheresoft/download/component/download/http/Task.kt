package com.beheresoft.download.component.download.http

import com.beheresoft.download.enums.DownLoadStatus

class Task(var size: Long = 0) {
    var speed: Int = 0
    var startTime: Long = 0
    var pauseTime: Long = 0
    val createTime: Long = System.currentTimeMillis()
    var downSize: Long = 0
    var supportBlock: Boolean = true
    var fileName: String? = null
    var status: DownLoadStatus = DownLoadStatus.WAIT

    fun addDownSize(size: Int) {
        downSize += size
    }
}