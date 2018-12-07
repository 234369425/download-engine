package com.beheresoft.test.kotlin

fun main(args: Array<String>) {
    val nb = 200
    when (nb) {
        in 200..299 -> {
            println(nb)
        }
        else -> {
            print("not in range")
        }
    }
}