package com.wawakaka.jst.camera.view

import com.wonderkiln.camerakit.Size
import java.io.File


/**
 * Created by wawakaka on 11/22/2017.
 */
object ResultHolder {

    var image: ByteArray? = null
    var video: File? = null
    var nativeCaptureSize: Size? = null
    var timeToCallback: Long = 0

    fun dispose() {
        video = null
        image = null
        nativeCaptureSize = null
        timeToCallback = 0
    }

}