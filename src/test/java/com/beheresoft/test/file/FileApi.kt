package com.beheresoft.test.file

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    println(Files.size(Paths.get("c:/")))
    println(Paths.get("c://").toFile().freeSpace)
}