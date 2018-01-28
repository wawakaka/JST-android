package com.wawakaka.jst.journal.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.journal.model.JournalRefreshListEvent
import com.wawakaka.jst.journal.model.Kegiatan
import com.wawakaka.jst.journal.model.KegiatanRequestWrapper
import io.reactivex.Observable

/**
 * Created by babang on 1/27/2018.
 */
class JournalPresenter(private val serverRequestManager: ServerRequestManager,
                       private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = JournalPresenter::class.java.simpleName
    }

    fun loadJournal(jadwalKelasId: Int): Observable<MutableList<Kegiatan>>? {
        return serverRequestManager
            .loadJournalObservable(jadwalKelasId)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveJournal(it) }
    }

    fun createJournal(kegiatan: KegiatanRequestWrapper?): Observable<Boolean> {
        return serverRequestManager
            .createJournalObservable(kegiatan)
            .map { it.data!! }
    }

    fun updateJournal(jadwalKelasId: Int,
                      kegiatan: KegiatanRequestWrapper?): Observable<Boolean> {
        return serverRequestManager
            .updateJournalObservable(jadwalKelasId, kegiatan)
            .map { it.data!! }
    }

    fun deleteJournal(kegiatanId: Int): Observable<Boolean> {
        return serverRequestManager
            .deleteJournalObservable(kegiatanId)
            .map { it.data!! }
    }

    fun publishJournalRefreshListEvent() {
        RxBus.post(JournalRefreshListEvent())
    }

    fun listenJournalRefreshListEvent() = RxBus.registerObservable<JournalRefreshListEvent>()

    private fun saveJournal(listJournal: MutableList<Kegiatan>) {
        localRequestManager.saveListJournal(listJournal)
    }

}