package com.wawakaka.jst.presensi.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.presensi.model.Presensi
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
            .loadPresensiCheckedListOBservable(idJadwalKelas)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() ?: true }
            .map { it.data!! }
            .doOnNext { savePresensiCheckedList(it) }
    }

    fun savePresensiCheckedListObservable(jadwalKelasId: Int?,
                                          presensi: MutableList<Presensi>): Observable<Boolean?> {
        return serverRequestManager
            .savePresensiCheckedListObservable(
                jadwalKelasId,
                presensi
            )
            .toResultEmptyErrorIfEmpty { it?.data ?: false }
            .map { it.data }
            .filter { it }
            .doOnNext { savePresensiCheckedList(presensi) }
    }

    private fun savePresensiCheckedList(list: List<Presensi>) {
        localRequestManager.savePresensiCheckedList(list)
    }
}