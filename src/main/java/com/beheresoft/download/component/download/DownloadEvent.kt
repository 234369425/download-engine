package com.beheresoft.download.component.download

interface DownloadEvent {
    fun start()
    fun pause()
    fun remove()
}
