package com.wawakaka.jst.admin.presenter

import com.wawakaka.jst.admin.model.*
import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 12/29/2017.
 */
class AdminPresenter(private val serverRequestManager: ServerRequestManager,
                     private val localRequestManager: LocalRequestManager) : BasePresenter() {

    fun loadAllBidangObservable(): Observable<MutableList<Bidang>> {
        return serverRequestManager
                .loadAllBidangObservable()
                .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
                .map { it.data!! }
                .doOnNext { localRequestManager.saveListBidang(it) }
    }

    fun addBidangObservable(bidang: Bidang): Observable<Boolean> {
        return serverRequestManager
                .addBidangObservable(BidangRequestWrapper(bidang))
                .map { it.data!! }
    }

    fun deleteBidangObservable(bidang: String): Observable<Boolean> {
        return serverRequestManager
                .deleteBidangObservable(bidang)
                .map { it.data!! }
    }

    fun loadAllJadwalKelasObservable(): Observable<MutableList<JadwalKelas>> {
        return serverRequestManager
                .loadAllJadwalKelasObservable()
                .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
                .map { it.data!! }
    }

    fun addJadwalKelasObservable(jadwalKelas: JadwalKelas): Observable<Boolean> {
        return serverRequestManager
                .addJadwalKelasObservable(
                        JadwalRequestWrapper(jadwalKelas)
                )
                .map { it.data!! }
    }

    fun updateJadwalKelasObservable(jadwalKelas: JadwalKelas): Observable<Boolean> {
        return serverRequestManager
                .updateJadwalKelasObservable(
                        jadwalKelas.id ?: 0,
                        JadwalRequestWrapper(jadwalKelas)
                )
                .map { it.data!! }
    }

    fun deleteJadwalKelasObservable(jadwalKelas: JadwalKelas): Observable<Boolean> {
        return serverRequestManager
                .deleteJadwalKelasObservable(
                        jadwalKelas.id ?: 0
                )
                .map { it.data!! }
    }

    fun getJadwalKelasUserObservable(jadwalKelas: JadwalKelas): Observable<User> {
        return serverRequestManager
                .getJadwalUserObservable(
                        jadwalKelas.id ?: 0
                )
                .map { it.data!! }
    }

    fun loadAllSiswaObservable(): Observable<MutableList<Siswa>> {
        return serverRequestManager
                .loadAllSiswaObservable()
                .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
                .map { it.data!! }
    }

    fun addSiswaObservable(siswa: Siswa): Observable<Boolean> {
        return serverRequestManager
                .addSiswaObservable(
                        SiswaRequestWrapper(siswa)
                )
                .map { it.data!! }
    }

    fun updateSiswaObservable(siswa: Siswa): Observable<Boolean> {
        return serverRequestManager
                .updateSiswaObservable(
                        siswa.id ?: "",
                        SiswaRequestWrapper(siswa)
                )
                .map { it.data!! }
    }

    fun updateStatusSiswaObservable(siswa: Siswa): Observable<Boolean> {
        return serverRequestManager
                .updateStatusSiswaObservable(
                        siswa.id ?: ""
                )
                .map { it.data!! }
    }

    fun loadAllKelasObservable(): Observable<MutableList<Kelas>> {
        return serverRequestManager
                .loadAllKelasObservable()
                .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
                .map { it.data!! }
                .doOnNext { localRequestManager.saveListKelas(it) }
    }

    fun updateStatusKelasObservable(id: Int): Observable<Boolean> {
        return serverRequestManager
                .updateStatusKelasObservable(id)
                .map { it.data!! }
    }

    fun getSekolah(): MutableList<Sekolah> {
        return localRequestManager.getListSekolah().toMutableList()
    }

    fun publishRefreshListBidangEvent() {
        RxBus.post(BidangListRefreshEvent())
    }

    fun listenRefreshListBidangEvent() = RxBus.registerObservable<BidangListRefreshEvent>()

    fun publishRefreshListJadwalKelasEvent() {
        RxBus.post(JadwalKelasRefreshListEvet())
    }

    fun listenRefreshListJadwalKelasEvent() = RxBus.registerObservable<JadwalKelasRefreshListEvet>()

    fun publishRefreshListSiswaEvent() {
        RxBus.post(SiswaRefreshListEvet())
    }

    fun listenRefreshListSiswaEvent() = RxBus.registerObservable<SiswaRefreshListEvet>()

    fun publishRefreshListKelasEvent() {
        RxBus.post(KelasRefreshListEvet())
    }

    fun listenRefreshListKelasEvent() = RxBus.registerObservable<KelasRefreshListEvet>()

}