package com.wawakaka.jst.base.utils

import com.wawakaka.jst.base.model.RxBusIdentifiedEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by wawakaka on 11/15/2017.
 */
object RxBus {

    val busPublishSubject: PublishSubject<Any> = PublishSubject.create<Any>()

    inline fun <reified T : Any> registerObservable(): Observable<T> {
        return busPublishSubject
            .filter { it.javaClass == T::class.java }
            .map({ obj -> obj as T })
    }

    inline fun <reified T : Any> registerObservable(busId: Int): Observable<T> {
        return busPublishSubject
            .filter {
                it is RxBusIdentifiedEvent
                    && it.busId == busId
                    && it.event.javaClass == T::class.java
            }
            .map { (it as RxBusIdentifiedEvent).event as T }
    }

    fun post(event: Any) {
        if (busPublishSubject.hasObservers()) {
            busPublishSubject.onNext(event)
        }
    }

    fun post(busId: Int, event: Any) {
        if (busPublishSubject.hasObservers()) {
            busPublishSubject.onNext(RxBusIdentifiedEvent(busId, event))
        }
    }

}