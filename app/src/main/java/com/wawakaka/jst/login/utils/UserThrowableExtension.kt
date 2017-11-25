package com.wawakaka.jst.login.utils

/**
 * Created by wawakaka on 11/7/2017.
 */
fun Throwable.isNotFoundError(httpErrorMessage: String?): Boolean {
    return message?.toLowerCase()?.contains("404") ?: false
        && httpErrorMessage?.contains("not found") ?: false
}