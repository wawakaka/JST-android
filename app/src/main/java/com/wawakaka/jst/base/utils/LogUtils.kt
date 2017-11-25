package com.wawakaka.jst.base.utils

import android.util.Log

/**
 * Created by wawakaka on 7/25/2017.
 */
object LogUtils {

    private var logTag: String? = null
    var logEnabled: Boolean = false

    fun enableLogging(logEnabled: Boolean, logTag: String) {
        this.logEnabled = logEnabled
        this.logTag = logTag
    }

    fun debug(tag: String, message: String) {
        if (logEnabled) {
            Log.d(logTag, tag + " - " + message)
        }
    }

    fun error(tag: String, message: String, throwable: Throwable) {
        if (logEnabled) {
            Log.e(logTag, tag + " - " + message, throwable)
        }
    }
}