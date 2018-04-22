package com.wawakaka.jst.tesHarian.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.tesHarian.model.*
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/27/2017.
 */
class TesHarianPresenter(val serverRequestManager: ServerRequestManager,
                         val localRequestManager: LocalRequestManager) : BasePresenter() {

    fun loadTesHarian(idJadwalKelas: Int,
                      tesHarian: TesHarian): Observable<TesHarian> {
        return serverRequestManager
            .loadTesHarianObservable(idJadwalKelas, TesHarianRequest(tesHarian))
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveTesHarian(it, idJadwalKelas) }
    }

    fun reloadTesHarian(idJadwalKelas: Int): Observable<TesHarian> {
        return serverRequestManager
            .reloadTesHarianObservable(idJadwalKelas)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveTesHarian(it, idJadwalKelas) }
    }

    fun updateTesHarian(tesHarian: TesHarian,
                        idJadwalKelas: Int): Observable<Boolean> {
        return serverRequestManager
            .updateTesHarianObservable(tesHarian)
            .map { it.data!! }
            .filter { it }
            .doOnNext { saveTesHarian(tesHarian, idJadwalKelas) }
    }

    fun updateHasilTesHarian(hasilTesHarian: HasilTesHarian): Observable<Boolean> {
        return serverRequestManager
            .updateHasilTesHarian(hasilTesHarian.id ?: 0, HasilTesHarianRequest(hasilTesHarian))
            .map { it.data!! }

    }

    private fun saveTesHarian(tesHarian: TesHarian,
                              idJadwalKelas: Int) {
        localRequestManager.saveTesHarian(tesHarian, idJadwalKelas)
    }

    fun getTesHarian(idJadwalKelas: Int): TesHarian {
        return localRequestManager.getTesHarian(idJadwalKelas)
    }

    fun getSiswaById(siswaId: String?): Siswa {
        return getSiswa().find { it.id == siswaId } ?: Siswa.empty
    }

    fun getSiswa(): List<Siswa> {
        return localRequestManager.getListSiswa()
    }

    fun listenTesHarianRefreshListEvent() = RxBus.registerObservable<TesHarianRefreshListEvent>()

    fun publishTesHarianRefreshListEvent() {
        RxBus.post(TesHarianRefreshListEvent())
    }

}