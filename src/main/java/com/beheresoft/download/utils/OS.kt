package com.beheresoft.download.utils

import java.net.ServerSocket

object OS {

    private val osName = System.getProperty("os.name").toLowerCase()
    private val osArch = System.getProperty("sun.arch.data.model")

    fun windows() = osName.contains("win")
    fun windowsXP() = osName.contains("win") && osName.contains("xp")
    fun mac() = osName.contains("mac")
    fun unix() = osName.contains("nix") || osName.contains("nux") || osName.contains("aix")
    fun solaris() = osName.contains("sunos")

    fun x86() = "32" == osArch
    fun x64() = "64" == osArch

    fun freePort() = ServerSocket().localPort


}