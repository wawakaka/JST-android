package com.wawakaka.jst.admin.presenter

import com.wawakaka.jst.admin.bidang.model.BidangListRefreshEvent
import com.wawakaka.jst.admin.bidang.model.BidangRequestWrapper
import com.wawakaka.jst.admin.event.model.RefreshListAdminEvent
import com.wawakaka.jst.admin.jadwalkelas.model.JadwalKelasRefreshListEvet
import com.wawakaka.jst.admin.jadwalkelas.model.JadwalRequestWrapper
import com.wawakaka.jst.admin.kelas.model.KelasRefreshListEvet
import com.wawakaka.jst.admin.kelas.model.KelasRequestWrapper
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.admin.sekolah.model.SekolahRefreshListEvet
import com.wawakaka.jst.admin.sekolah.model.SekolahRequestWrapper
import com.wawakaka.jst.admin.siswa.model.SiswaRefreshListEvet
import com.wawakaka.jst.admin.siswa.model.SiswaRequestWrapper
import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.event.model.Event
import com.wawakaka.jst.event.model.EventRequestWrapper
import com.wawakaka.jst.event.presenter.EventPresenter
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 12/29/2017.
 */
class AdminPresenter(private val serverRequestManager: ServerRequestManager,
                     private val localRequestManager: LocalRequestManager,
                     private val eventPresenter: EventPresenter) : BasePresenter() {

    fun loadAllBidangObservable(): Observable<MutableList<Bidang>> {
        return serverRequestManager
            .loadAllBidangObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveBidang(it) }
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

    fun loadJadwalKelasUserObservable(jadwalKelas: JadwalKelas): Observable<User> {
        return serverRequestManager
            .loadJadwalUserObservable(
                jadwalKelas.id ?: 0
            )
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
    }

    fun loadAllSiswaObservable(): Observable<MutableList<Siswa>> {
        return serverRequestManager
            .loadAllSiswaObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveSiswa(it) }
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
            .doOnNext { saveKelas(it) }
    }

    fun addKelasObservable(kelas: Kelas): Observable<Boolean> {
        return serverRequestManager
            .addKelasObservable(KelasRequestWrapper(kelas))
            .map { it.data!! }

    }

    fun updateKelasObservable(id: Int,
                              kelas: Kelas): Observable<Boolean> {
        return serverRequestManager
            .updateKelasObservable(
                id,
                KelasRequestWrapper(kelas)
            )
            .map { it.data!! }
    }

    fun updateStatusKelasObservable(id: Int): Observable<Boolean> {
        return serverRequestManager
            .updateStatusKelasObservable(id)
            .map { it.data!! }
    }

    fun loadAllUser(): Observable<MutableList<User>> {
        return serverRequestManager
            .loadAllUserObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveUsers(it) }
    }

    fun loadAllSekolah(): Observable<MutableList<Sekolah>> {
        return serverRequestManager
            .loadAllSekolahObservable()
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() != false }
            .map { it.data!! }
            .doOnNext { saveSekolah(it) }
    }

    fun addSekolahObservable(sekolah: Sekolah): Observable<Boolean> {
        return serverRequestManager
            .addSekolahObservable(SekolahRequestWrapper(sekolah))
            .map { it.data!! }
    }

    fun deleteSekolahObservable(sekolah: String): Observable<Boolean> {
        return serverRequestManager
            .deleteSekolahObservable(sekolah)
            .map { it.data!! }
    }

    fun loadAllEvent(): Observable<MutableList<Event>> {
        return eventPresenter.loadAllEventObservable()
    }

    fun createEventObservable(event: EventRequestWrapper): Observable<Boolean> {
        return eventPresenter.createEventObservable(event)
    }

    fun updateEventObservable(id: Int, event: EventRequestWrapper): Observable<Boolean> {
        return eventPresenter.updateEventObservable(id, event)
    }

    fun deleteEventObservable(id: Int): Observable<Boolean> {
        return eventPresenter.deleteEventObservable(id)
    }

    fun getSiswa(): MutableList<Siswa> {
        return localRequestManager.getListSiswa().toMutableList()
    }

    private fun saveSiswa(siswa: MutableList<Siswa>) {
        localRequestManager.saveListSiswa(siswa)
    }

    fun getUsers(): MutableList<User> {
        return localRequestManager.getListUser().toMutableList()
    }

    private fun saveUsers(users: MutableList<User>) {
        localRequestManager.saveListUser(users)
    }

    fun getSekolah(): MutableList<Sekolah> {
        return localRequestManager.getListSekolah().toMutableList()
    }

    private fun saveSekolah(sekolah: MutableList<Sekolah>) {
        localRequestManager.saveListSekolah(sekolah)
    }

    fun getBidang(): MutableList<Bidang> {
        return localRequestManager.getListBidang().toMutableList()
    }

    private fun saveBidang(bidang: MutableList<Bidang>) {
        localRequestManager.saveListBidang(bidang)
    }

    fun getKelas(): MutableList<Kelas> {
        return localRequestManager.getListKelas().toMutableList()
    }

    private fun saveKelas(kelas: MutableList<Kelas>) {
        localRequestManager.saveListKelas(kelas)
    }

    private fun saveEvent(event: MutableList<Event>) {
        localRequestManager.saveListEvent(event)
    }

    fun getListEvent(): MutableList<Event> {
        return localRequestManager.getListEvent().toMutableList()
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

    fun publishRefreshListSekolahEvent() {
        RxBus.post(SekolahRefreshListEvet())
    }

    fun listenRefreshListSekolahEvent() = RxBus.registerObservable<SekolahRefreshListEvet>()

    fun publishRefreshListAdminEvent() {
        RxBus.post(RefreshListAdminEvent())
    }

    fun listenRefreshListAdminEvent() = RxBus.registerObservable<RefreshListAdminEvent>()

}