package com.wawakaka.jst.datasource.server.model

/**
 * Created by wawakaka on 7/25/2017.
 */
open class ServerRequestError(protected val throwable: Throwable) : Throwable(throwable)
