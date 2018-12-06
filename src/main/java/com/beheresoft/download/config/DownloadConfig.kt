package com.beheresoft.download.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "system")
class DownloadConfig {
    var savePath: String = ""
    var speedLimit: Long = -1
    var timeout: Long = 60
}