package com.wawakaka.jst.presensi.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.presensi.model.PresensiRequestWrapper
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/27/2017.
 */
class PresensiPresenter(private val serverRequestManager: ServerRequestManager,
                        private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = PresensiPresenter::class.java.simpleName
    }

    fun loadPresensiCheckedListObservable(idJadwalKelas: Int?): Observable<MutableList<Presensi>> {
        return serverRequestManager
            .loadPresensiCheckedListObservable(idJadwalKelas)
            .map { it.data!! }
            .doOnNext { savePresensiCheckedList(it) }
    }

    fun savePresensiCheckedListObservable(jadwalKelasId: Int?,
                                          request: PresensiRequestWrapper): Observable<Boolean> {
        return serverRequestManager
            .savePresensiCheckedListObservable(
                jadwalKelasId,
                request
            )
            .map { it.data!! }
            .doOnNext { savePresensiCheckedList(request.presensi) }
    }

    private fun savePresensiCheckedList(list: List<Presensi>) {
        localRequestManager.savePresensiCheckedList(list)
    }
}