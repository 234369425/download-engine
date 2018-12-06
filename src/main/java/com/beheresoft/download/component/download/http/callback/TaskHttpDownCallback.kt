package com.beheresoft.download.component.download.http.callback

import com.beheresoft.download.component.download.http.HttpDownloadBootStrap
import com.beheresoft.download.component.download.http.entity.Block
import org.slf4j.LoggerFactory

class TaskHttpDownCallback : Callback {

    val log = LoggerFactory.getLogger(TaskHttpDownCallback::class.java)

    override fun onStart(bootstrap: HttpDownloadBootStrap) {
        log.info("task start")
    }

    override fun onProgress(bootstrap: HttpDownloadBootStrap) {

    }

    override fun onPause(bootstrap: HttpDownloadBootStrap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume(bootstrap: HttpDownloadBootStrap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockError(bootstrap: HttpDownloadBootStrap, block: Block) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(bootstrap: HttpDownloadBootStrap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockDone(bootstrap: HttpDownloadBootStrap, block: Block) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDone(bootstrap: HttpDownloadBootStrap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}