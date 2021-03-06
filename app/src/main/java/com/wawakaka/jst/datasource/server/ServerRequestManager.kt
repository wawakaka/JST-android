package com.wawakaka.jst.datasource.server

import com.wawakaka.jst.admin.bidang.model.BidangRequestWrapper
import com.wawakaka.jst.admin.jadwalkelas.model.JadwalRequestWrapper
import com.wawakaka.jst.admin.kelas.model.KelasRequestWrapper
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.admin.sekolah.model.SekolahRequestWrapper
import com.wawakaka.jst.admin.siswa.model.SiswaRequestWrapper
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.model.*
import com.wawakaka.jst.datasource.server.utils.isServerRequestErrorNetwork
import com.wawakaka.jst.datasource.server.utils.isServerRequestErrorNoInternet
import com.wawakaka.jst.datasource.server.utils.isServerRequestErrorUnauthorized
import com.wawakaka.jst.event.model.Event
import com.wawakaka.jst.event.model.EventRequestWrapper
import com.wawakaka.jst.journal.model.Kegiatan
import com.wawakaka.jst.journal.model.KegiatanRequestWrapper
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.login.model.UserLoginRequest
import com.wawakaka.jst.pengeluaran.model.Pengeluaran
import com.wawakaka.jst.pengeluaran.model.PengeluaranRequestWrapper
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.presensi.model.PresensiRequestWrapper
import com.wawakaka.jst.tesHarian.model.HasilTesHarianRequest
import com.wawakaka.jst.tesHarian.model.TesHarian
import com.wawakaka.jst.tesHarian.model.TesHarianRequest
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by wawakaka on 7/25/2017.
 */
class ServerRequestManager(private val localRequestManager: LocalRequestManager,
                           private val serverApi: ServerApi) {

    companion object {
        val TAG: String = ServerRequestManager::class.java.simpleName
    }

    fun loadAllUserObservable(): Observable<ServerResponseWrapper<MutableList<User>>> {
        return handleServerRequestError(
            serverApi.loadAllUserObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun loginObservable(user: UserLoginRequest): Observable<ServerResponseWrapper<User>> {
        return handleServerRequestError(
            serverApi.loginObservable(
                getAcceptApplicationJsonHeader(),
                user.user?.email,
                user
            )
        )
    }

    fun updateUserObservable(user: User): Observable<ServerResponseWrapper<User>> {
        return handleServerRequestError(
            serverApi.updateUserObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                user.email,
                user
            )
        )
    }

    fun loadClassObservable(user: User): Observable<ServerResponseWrapper<MutableList<Kelas>>> {
        return handleServerRequestError(
            serverApi.loadKelasObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                user.email
            )
        )
    }

    fun loadKelasByEventObservable(eventId: Int): Observable<ServerResponseWrapper<MutableList<Kelas>>> {
        return handleServerRequestError(
            serverApi.loadKelasByEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                eventId
            )
        )
    }

    fun loadPresensiCheckedListObservable(idJadwalKelas: Int?): Observable<ServerResponseWrapper<MutableList<Presensi>>> {
        return handleServerRequestError(
            serverApi.loadPresensiCheckedListObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                idJadwalKelas
            )
        )
    }

    fun savePresensiCheckedListObservable(jadwalKelasId: Int?,
                                          request: PresensiRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.savePresensiCheckedListObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                jadwalKelasId,
                request
            )
        )
    }

    fun loadTesHarianObservable(idJadwalKelas: Int?,
                                tesHarian: TesHarianRequest): Observable<ServerResponseWrapper<TesHarian>> {
        return handleServerRequestError(
            serverApi.loadTesHarianObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                idJadwalKelas,
                tesHarian
            )
        )
    }

    fun reloadTesHarianObservable(idJadwalKelas: Int?): Observable<ServerResponseWrapper<TesHarian>> {
        return handleServerRequestError(
            serverApi.reloadTesHarianObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                idJadwalKelas
            )
        )
    }

    fun updateTesHarianObservable(tesHarian: TesHarian?): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateTesHarianObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                tesHarian!!.id,
                tesHarian
            )
        )
    }

    fun loadAllSiswaObservable(): Observable<ServerResponseWrapper<MutableList<Siswa>>> {
        return handleServerRequestError(
            serverApi.loadAllSiswaObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun addSiswaObservable(siswa: SiswaRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.addSiswaObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                siswa
            )
        )
    }

    fun updateSiswaObservable(id: String,
                              siswa: SiswaRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateSiswaObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                siswa
            )
        )
    }

    fun updateStatusSiswaObservable(id: String): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateStatusSiswaObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun updateHasilTesHarian(tesharianId: Int,
                             hasilTesHarianRequest: HasilTesHarianRequest): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateHasilTesHarian(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                tesharianId,
                hasilTesHarianRequest
            )
        )
    }

    fun loadAllBidangObservable(): Observable<ServerResponseWrapper<MutableList<Bidang>>> {
        return handleServerRequestError(
            serverApi.loadAllBidangObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun addBidangObservable(bidang: BidangRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createBidangObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                bidang
            )
        )
    }

    fun deleteBidangObservable(bidang: String): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deleteBidangObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                bidang
            )
        )
    }

    fun loadAllKelasObservable(): Observable<ServerResponseWrapper<MutableList<Kelas>>> {
        return handleServerRequestError(
            serverApi.loadAllKelasObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun addKelasObservable(kelas: KelasRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.addKelasObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                kelas
            )
        )
    }

    fun updateKelasObservable(id: Int,
                              kelas: KelasRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateKelasObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                kelas
            )
        )
    }

    fun updateStatusKelasObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateStatusKelasObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun loadAllJadwalKelasObservable(): Observable<ServerResponseWrapper<MutableList<JadwalKelas>>> {
        return handleServerRequestError(
            serverApi.loadAllJadwalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun addJadwalKelasObservable(jadwalKelas: JadwalRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createJadwalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                jadwalKelas
            )
        )
    }

    fun updateJadwalKelasObservable(id: Int,
                                    jadwalKelas: JadwalRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateJadwalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                jadwalKelas
            )
        )
    }

    fun deleteJadwalKelasObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deleteJadwalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun loadJadwalUserObservable(idJadwal: Int): Observable<ServerResponseWrapper<User>> {
        return handleServerRequestError(
            serverApi.loadJadwalUserObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                idJadwal
            )
        )
    }

    fun loadAllSekolahObservable(): Observable<ServerResponseWrapper<MutableList<Sekolah>>> {
        return handleServerRequestError(
            serverApi.loadAllSekolahObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun addSekolahObservable(sekolah: SekolahRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createSekolahObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                sekolah
            )
        )
    }

    fun deleteSekolahObservable(sekolah: String): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deleteSekolahObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                sekolah
            )
        )
    }

    fun loadJournalObservable(id: Int): Observable<ServerResponseWrapper<MutableList<Kegiatan>>> {
        return handleServerRequestError(
            serverApi.loadJournalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun createJournalObservable(kegiatan: KegiatanRequestWrapper?): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createJournalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                kegiatan
            )
        )
    }

    fun updateJournalObservable(id: Int,
                                kegiatan: KegiatanRequestWrapper?): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateJournalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                kegiatan
            )
        )
    }

    fun deleteJournalObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deleteJournalObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun loadAllPengeluaranObservable(): Observable<ServerResponseWrapper<MutableList<Pengeluaran>>> {
        return handleServerRequestError(
            serverApi.loadAllPengeluaranObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun loadPengeluaranObservable(eventId: Int): Observable<ServerResponseWrapper<MutableList<Pengeluaran>>> {
        return handleServerRequestError(
            serverApi.loadPengeluaranObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                eventId
            )
        )
    }

    fun createPengeluaranObservable(pengeluaran: PengeluaranRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createPengeluaranObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                pengeluaran
            )
        )
    }

    fun updatePengeluaranObservable(id: Int,
                                    pengeluaran: PengeluaranRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updatePengeluaranObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                pengeluaran
            )
        )
    }

    fun deletePengeluaranObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deletePengeluaranObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun loadEventObservable(): Observable<ServerResponseWrapper<MutableList<Event>>> {
        return handleServerRequestError(
            serverApi.loadEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader()
            )
        )
    }

    fun loadEventObservable(id: Int): Observable<ServerResponseWrapper<Event>> {
        return handleServerRequestError(
            serverApi.loadEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }

    fun createEventObservable(pengeluaran: EventRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.createEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                pengeluaran
            )
        )
    }

    fun updateEventObservable(id: Int,
                              pengeluaran: EventRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id,
                pengeluaran
            )
        )
    }

    fun deleteEventObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.deleteEventObservable(
                getAccessTokenHeader(),
                getAcceptApplicationJsonHeader(),
                id
            )
        )
    }
//
//    fun loadAllBidangUserObservable(): Observable<ServerResponseWrapper<MutableList<BidangUser>>> {
//        return handleServerRequestError(
//            serverApi.loadBidangUserObservable(
//                getAccessTokenHeader(),
//                getAcceptApplicationJsonHeader()
//            )
//        )
//    }
//
//    fun loadBidangUserObservable(email: String): Observable<ServerResponseWrapper<MutableList<BidangUser>>> {
//        return handleServerRequestError(
//            serverApi.loadBidangUserObservable(
//                getAccessTokenHeader(),
//                getAcceptApplicationJsonHeader(),
//                email
//            )
//        )
//    }
//
//    fun createBidangUserObservable(bidangUserRequestWrapper: BidangUserRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
//        return handleServerRequestError(
//            serverApi.createBidangUserObservable(
//                getAccessTokenHeader(),
//                getAcceptApplicationJsonHeader(),
//                bidangUserRequestWrapper
//            )
//        )
//    }
//
//    fun updateBidangUserObservable(id: Int,
//                                   bidangUserRequestWrapper: BidangUserRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
//        return handleServerRequestError(
//            serverApi.updateBidangUserObservable(
//                getAccessTokenHeader(),
//                getAcceptApplicationJsonHeader(),
//                id,
//                bidangUserRequestWrapper
//            )
//        )
//    }
//
//    fun deleteBidangUserObservable(id: Int): Observable<ServerResponseWrapper<Boolean>> {
//        return handleServerRequestError(
//            serverApi.deleteBidangUserObservable(
//                getAccessTokenHeader(),
//                getAcceptApplicationJsonHeader(),
//                id
//            )
//        )
//    }

    /**
     * Handle error occurred from original observable.
     *
     * @param originalObservable Observable original that will be executed immediately
     */
    private fun <T> handleServerRequestError(originalObservable: Observable<T>): Observable<T> {
        return originalObservable.onErrorResumeNext(Function<Throwable, Observable<T>> {
            when {
                it.isServerRequestErrorUnauthorized() -> {
                    RxBus.post(InvalidTokenError(it)) // Broadcast event to force logout
                    Observable.error(InvalidTokenError(it))
                }
                it.isServerRequestErrorNoInternet() -> Observable.error(NoInternetError(it))
                it.isServerRequestErrorNetwork() -> Observable.error(NetworkError(it))
                else -> Observable.error(UnknownError(it))
            }
        })
    }

    private fun getAcceptApplicationJsonHeader(): String {
        return "application/json"
    }

    private fun getAccessTokenHeader(): String {
        return "Bearer ${localRequestManager.getUser().token ?: ""}"
    }

}