package com.beheresoft.test.file

import com.beheresoft.download.utils.FileOperate
import org.junit.Assert
import org.junit.Test

class FileUtils {

    @Test
    fun test() {
        println(FileOperate.genFileName("f:/", "wangdi", ""))
        val (name, ext) = FileOperate.partitionName("abc.txt")
        Assert.assertEquals(name, "abc")
        Assert.assertEquals(ext, "txt")
        val (n1, e1) = FileOperate.partitionName("abc")
        Assert.assertEquals(n1, "abc")
        Assert.assertEquals(e1, "")
    }

}
