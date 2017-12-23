package com.wawakaka.jst.dailytest.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dailytest.model.TesHarian
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/27/2017.
 */
class DailyTestPresenter(val serverRequestManager: ServerRequestManager,
                         val localRequestManager: LocalRequestManager) : BasePresenter() {

    fun loadTesHarian(idJadwalKelas: Int): Observable<TesHarian> {
        return serverRequestManager
            .loadTesHarianOBservable(idJadwalKelas)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() ?: true }
            .map { it.data!! }
            .doOnNext { saveTesHarian(it) }
    }

    fun updateTesHarian(tesHarian: TesHarian): Observable<Boolean> {
        return serverRequestManager
            .updateTesHarianOBservable(tesHarian)
            .map { it.data!! }
            .filter { it }
            .doOnNext { saveTesHarian(tesHarian) }
    }

    private fun saveTesHarian(tesHarian: TesHarian) {
        localRequestManager.saveTesHarian(tesHarian)
    }

    fun getTesHarian(): TesHarian {
        return localRequestManager.getTesHarian()
    }

    fun getSiswaById(siswaId: String?): Siswa {
        return getSiswa().find { it.id == siswaId } ?: Siswa.empty
    }

    fun getSiswa(): List<Siswa> {
        return localRequestManager.getListSiswa()
    }

}