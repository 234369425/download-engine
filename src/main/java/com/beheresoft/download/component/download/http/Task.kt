package com.beheresoft.download.component.download.http

import com.beheresoft.download.component.download.http.entity.Block
import com.beheresoft.download.component.download.http.entity.Request
import com.beheresoft.download.enums.DownLoadStatus
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class Task(var size: Long = 0, var request: Request, var loopGroup: NioEventLoopGroup) {

    var speed: Int = 0
    var startTime: Long = 0
    var pauseTime: Long = 0
    val createTime: Long = System.currentTimeMillis()
    var downSize: Long = 0
    var supportBlock: Boolean = true
    var fileName: String? = null
    var status: DownLoadStatus = DownLoadStatus.WAIT
    var blocks: ArrayList<Block> = ArrayList()
    private var progress: ProgressThread? = null
    private val log = LoggerFactory.getLogger(Task::class.java)

    fun addBlock(b: Block) {
        blocks.add(b)
    }

    fun addDownSize(size: Int) {
        downSize += size
    }

    fun start() {
        if (progress == null) {
            progress = ProgressThread(this)
            progress?.start()
        }

        startTime = System.currentTimeMillis()
        status = DownLoadStatus.DOWNING
        blocks.forEach {
            it.resetErrorTime()
            it.start()
        }

    }

    fun pause() {
        speed = 0
        progress?.exit()
    }

    fun remove() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //计算瞬时速度
    class ProgressThread(private val task: Task, private val period: Long = 100) : Thread() {

        private var run = true
        private val log = LoggerFactory.getLogger(ProgressThread::class.java)

        override fun run() {
            while (run) {
                if (task.status != DownLoadStatus.DONE) {
                    var speed = 0
                    task.blocks.forEach {
                        if (it.status != DownLoadStatus.DONE) {
                            speed += it.getSpeed()
                        }
                    }
                    task.speed = speed
                } else {
                    run = false
                }
                TimeUnit.MILLISECONDS.sleep(period)
            }
        }

        fun exit() {
            run = false
        }
    }
}