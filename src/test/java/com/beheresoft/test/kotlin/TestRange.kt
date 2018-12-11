package com.beheresoft.test.kotlin

fun main(args: Array<String>) {

    fun newLine(){
        println()
        println("---------------------------")
    }

    0.rangeTo(9).forEach {
        print(it)
        print(",")
    }
    newLine()
    'a'.rangeTo('z').forEach {
        print(it)
        print(",")
    }
    newLine()
    println("半开区间")
    (0 until 9).forEach {
        print(it)
        print(",")
    }
    newLine()
    println("半开步长区间")
    (0 until 9 step 3).forEach{
        print(it)
        print(",")
    }
    newLine()
    println("倒序区间")
    for( i in 9 downTo 1){
        print(i)
        print(",")
    }
    newLine()

}