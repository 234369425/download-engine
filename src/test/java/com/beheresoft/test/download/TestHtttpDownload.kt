package com.beheresoft.test.download

import com.beheresoft.download.component.download.http.HttpDownloadBootStrap
import com.beheresoft.download.config.DownloadConfig

fun main(args: Array<String>) {

    HttpDownloadBootStrap("http://pji29xhhg.bkt.clouddn.com/NDP462-KB3151800-x86-x64-AllOS-ENU.exe"
    , DownloadConfig()
    ).start()
}