package com.beheresoft.download.component.download.http.callback

import com.beheresoft.download.component.download.http.HttpDownloadBootStrap
import com.beheresoft.download.component.download.http.entity.Block

interface Callback {

    fun onStart(bootstrap: HttpDownloadBootStrap)

    fun onProgress(bootstrap: HttpDownloadBootStrap)

    fun onPause(bootstrap: HttpDownloadBootStrap)

    fun onResume(bootstrap: HttpDownloadBootStrap)

    fun onBlockError(bootstrap: HttpDownloadBootStrap, block: Block)

    fun onError(bootstrap: HttpDownloadBootStrap)

    fun onBlockDone(bootstrap: HttpDownloadBootStrap, block: Block)

    fun onDone(bootstrap: HttpDownloadBootStrap)

}