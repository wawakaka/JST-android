package com.wawakaka.jst.event.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.event.model.Event
import com.wawakaka.jst.event.model.EventListRefreshEvent
import com.wawakaka.jst.event.model.EventRequestWrapper
import io.reactivex.Observable

class EventPresenter(private val serverRequestManager: ServerRequestManager,
                     private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = EventPresenter::class.java.simpleName
    }

    fun loadAllEventObservable(): Observable<MutableList<Event>> {
        return serverRequestManager
            .loadEventObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() == true }
            .map { it.data!! }
            .doOnNext { saveEvent(it) }
    }

    fun createEventObservable(event: EventRequestWrapper): Observable<Boolean> {
        return serverRequestManager
            .createEventObservable(event)
            .map { it.data!! }
    }

    fun updateEventObservable(id: Int,
                              event: EventRequestWrapper): Observable<Boolean> {
        return serverRequestManager
            .updateEventObservable(id, event)
            .map { it.data!! }
    }

    fun deleteEventObservable(id: Int): Observable<Boolean> {
        return serverRequestManager
            .deleteEventObservable(id)
            .map { it.data!! }
    }

    private fun saveEvent(event: MutableList<Event>) {
        localRequestManager.saveListEvent(event)
    }

    fun getListEvent(): MutableList<Event> {
        return localRequestManager.getListEvent().toMutableList()
    }

    fun listenRefreshListEvent() = RxBus.registerObservable<EventListRefreshEvent>()

    fun publishRefreshListEvent() {
        RxBus.post(EventListRefreshEvent())
    }
}