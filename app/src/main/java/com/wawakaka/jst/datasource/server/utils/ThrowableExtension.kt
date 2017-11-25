package com.wawakaka.jst.datasource.server.utils


import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

/**
 * Created by elsennovraditya on 12/13/16.
 */

fun Throwable.isServerRequestErrorUnauthorized(): Boolean {
    return this is HttpException && this.code() == 401
}

fun Throwable.isServerRequestErrorNoInternet(): Boolean {
    return this is UnknownHostException
}

fun Throwable.isServerRequestErrorNetwork(): Boolean {
    return this is IOException || (this.message?.contains("NetworkNotAvailable") ?: false)
}

fun Throwable.getHttpErrorMessage(): String? {
    if (this is HttpException) {
        return this.response().errorBody()?.string()
    }

    val throwableCause = this.cause
    if (throwableCause is HttpException) {
        return throwableCause.response().errorBody()?.string()
    }

    return null
}