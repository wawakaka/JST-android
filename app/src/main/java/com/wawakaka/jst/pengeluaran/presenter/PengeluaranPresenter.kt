package com.wawakaka.jst.pengeluaran.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.pengeluaran.model.*
import io.reactivex.Observable

/**
 * Created by babang on 1/28/2018.
 */
class PengeluaranPresenter(private val serverRequestManager: ServerRequestManager,
                           private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = PengeluaranPresenter::class.java.simpleName
    }

    var listPengeluaran: MutableList<Pengeluaran> = mutableListOf()

    fun loadPengeluaranEventObservable(eventId: Int?): Observable<MutableList<Pengeluaran>> {
        return serverRequestManager
            .loadPengeluaranObservable(eventId ?: 0)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { listPengeluaran = it }
    }

    fun createPengeluaranObservable(pengeluaran: PengeluaranRequestWrapper): Observable<Boolean> {
        return serverRequestManager
            .createPengeluaranObservable(pengeluaran)
            .map { it.data!! }
    }

    fun updatePengeluaranObservable(id: Int,
                                    pengeluaran: PengeluaranRequestWrapper): Observable<Boolean> {
        return serverRequestManager
            .updatePengeluaranObservable(id, pengeluaran)
            .map { it.data!! }
    }

    fun deletePengeluaranObservable(id: Int): Observable<Boolean> {
        return serverRequestManager
            .deletePengeluaranObservable(id)
            .map { it.data!! }
    }

    fun publishRefreshListPengeluaranEvent() {
        RxBus.post(PengeluaranListRefreshEvent())
    }

    fun listenRefreshListPengeluaranEvent() = RxBus.registerObservable<PengeluaranListRefreshEvent>()

    fun publishUploadImageSuccessEvent() {
        RxBus.post(UploadImageSuccessEvent())
    }

    fun listenUploadImageSuccessEvent() = RxBus.registerObservable<UploadImageSuccessEvent>()

    fun publishUploadImageFailedEvent() {
        RxBus.post(UploadImageFailedEvent())
    }

    fun listenUploadImageFailedEvent() = RxBus.registerObservable<UploadImageFailedEvent>()

    fun getUser(): User = localRequestManager.getUser()

}