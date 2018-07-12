package com.wawakaka.jst.dashboard.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.KelasListRefreshEvent
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.event.model.Event
import com.wawakaka.jst.event.presenter.EventPresenter
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/13/2017.
 */
class DashboardPresenter(private val serverRequestManager: ServerRequestManager,
                         private val localRequestManager: LocalRequestManager,
                         private val eventPresenter: EventPresenter) : BasePresenter() {

    companion object {
        private val TAG = DashboardPresenter::class.java.simpleName
    }

    fun loadClassObservable(): Observable<MutableList<Kelas>> {
        return serverRequestManager
            .loadClassObservable(getUser())
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() == true }
            .map { it.data?.sortedBy { it.id }?.toMutableList() ?: mutableListOf() }
            .doOnNext { saveKelas(it) }
    }

    fun loadClassByEventObservable(eventId: Int): Observable<MutableList<Kelas>> {
        return serverRequestManager
            .loadKelasByEventObservable(eventId)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() == true }
            .map { it.data?.sortedBy { it.id }?.toMutableList() ?: mutableListOf() }
            .doOnNext { saveKelas(it) }
    }

    fun loadSiswaObservable(): Observable<MutableList<Siswa>> {
        return serverRequestManager
            .loadAllSiswaObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() == true }
            .map { it.data?.sortedBy { it.id }?.toMutableList() ?: mutableListOf() }
            .doOnNext { saveSiswa(it) }
    }

    fun getEventList(): MutableList<Event> {
        return eventPresenter.getListEvent()
    }

    fun getUser(): User = localRequestManager.getUser()

    private fun saveKelas(listKelas: List<Kelas>) {
        localRequestManager.saveListKelas(listKelas)
    }

    fun saveSiswa(listSiswa: List<Siswa>) {
        localRequestManager.saveListSiswa(listSiswa)
    }

    fun listenRefreshListKelasEvent() = RxBus.registerObservable<KelasListRefreshEvent>()

    fun publishRefreshListKelasEvent() {
        RxBus.post(KelasListRefreshEvent())
    }


}