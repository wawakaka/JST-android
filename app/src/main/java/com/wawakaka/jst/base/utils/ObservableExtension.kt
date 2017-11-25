package com.wawakaka.jst.base.utils

import com.wawakaka.jst.datasource.model.ResultEmptyError
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/6/2017.
 */

/**
 * Transform observable of [T] into observable error of [ResultEmptyError] if the item is
 * considered empty by the [emptyCheckingFunc]
 */
fun <T> Observable<T>.toResultEmptyErrorIfEmpty(emptyCheckingFunc: (obj: T?) -> Boolean): Observable<T> {
    return flatMap {
        if (emptyCheckingFunc(it)) {
            Observable.error<T>(ResultEmptyError(Throwable()))
        } else {
            Observable.just(it)
        }
    }
}