package com.beheresoft.download.component.download.http.callback

import com.beheresoft.download.component.download.http.entity.Block

interface Callback {

    fun onStart()

    fun onProgress()

    fun onPause()

    fun onResume()

    fun onBlockError(block: Block)

    fun onError()

    fun onBlockDone(block: Block)

    fun onDone()

}