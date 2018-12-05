package com.beheresoft.download.entity

import com.beheresoft.download.enums.DownLoadStatus

data class Task(var size: Long = 0, var createTime: Long = 0) {
    var speed: Int = 0
    var startTime: Long = 0
    var pauseTime: Long = 0
    var downSize: Long = 0
    var status: DownLoadStatus = DownLoadStatus.WAIT

    fun addDownSize(size: Int) {
        downSize += size
    }


}