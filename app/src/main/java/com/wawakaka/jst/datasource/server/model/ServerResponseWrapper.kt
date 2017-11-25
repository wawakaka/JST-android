package com.wawakaka.jst.datasource.server.model

/**
 * Created by wawakaka on 7/25/2017.
 */
class ServerResponseWrapper<out T>(val status: String?,
                                   val message: String?,
                                   val data: T?)
