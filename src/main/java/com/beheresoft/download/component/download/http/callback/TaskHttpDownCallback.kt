package com.beheresoft.download.component.download.http.callback

import com.beheresoft.download.component.download.http.entity.Block
import org.slf4j.LoggerFactory

class TaskHttpDownCallback : Callback {

    val log = LoggerFactory.getLogger(TaskHttpDownCallback::class.java)

    override fun onStart() {
        log.info("task start")
    }

    override fun onProgress() {

    }

    override fun onPause() {
        log.info("task pause ")
    }

    override fun onResume() {
        log.info("task resume ")

    }

    override fun onBlockError(block: Block) {
        log.info("block error ")

    }

    override fun onError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockDone( block: Block) {
        log.info("block done ")

    }

    override fun onDone() {
        log.info("task done")
    }

}