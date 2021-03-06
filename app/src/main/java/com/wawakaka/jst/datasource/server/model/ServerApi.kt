package com.wawakaka.jst.datasource.server.model

import com.wawakaka.jst.admin.bidang.model.BidangRequestWrapper
import com.wawakaka.jst.admin.jadwalkelas.model.JadwalRequestWrapper
import com.wawakaka.jst.admin.kelas.model.KelasRequestWrapper
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.admin.sekolah.model.SekolahRequestWrapper
import com.wawakaka.jst.admin.siswa.model.SiswaRequestWrapper
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
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
import retrofit2.http.*

/**
 * Created by wawakaka on 7/25/2017.
 */
interface ServerApi {

    //    route user
    @GET("user/all")
    fun loadAllUserObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<User>>>

    @POST("user/login/{email}")
    fun loginObservable(@Header("Content-Type") acceptFormat: String,
                        @Path("email") email: String?,
                        @Body user: UserLoginRequest): Observable<ServerResponseWrapper<User>>

    @PUT("user/{email}")
    fun updateUserObservable(@Header("Authorization") token: String,
                             @Header("Content-Type") acceptFormat: String,
                             @Path("email") email: String?,
                             @Body user: User): Observable<ServerResponseWrapper<User>>

    @GET("kelas/{email}")
    fun loadKelasObservable(@Header("Authorization") token: String,
                            @Header("Content-Type") acceptFormat: String,
                            @Path("email") email: String?): Observable<ServerResponseWrapper<MutableList<Kelas>>>

    @GET("kelas/event/{eventId}")
    fun loadKelasByEventObservable(@Header("Authorization") token: String,
                                   @Header("Content-Type") acceptFormat: String,
                                   @Path("eventId") eventId: Int?): Observable<ServerResponseWrapper<MutableList<Kelas>>>

    @GET("/presensi/{id}")
    fun loadPresensiCheckedListObservable(@Header("Authorization") token: String,
                                          @Header("Content-Type") acceptFormat: String,
                                          @Path("id") id: Int?): Observable<ServerResponseWrapper<MutableList<Presensi>>>

    @POST("/presensi/{id}/update")
    fun savePresensiCheckedListObservable(@Header("Authorization") token: String,
                                          @Header("Content-Type") acceptFormat: String,
                                          @Path("id") id: Int?,
                                          @Body request: PresensiRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @POST("/tesharian/{id}")
    fun loadTesHarianObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Path("id") id: Int?,
                                @Body tesHarian: TesHarianRequest): Observable<ServerResponseWrapper<TesHarian>>

    @GET("/tesharian/{id}")
    fun reloadTesHarianObservable(@Header("Authorization") token: String,
                                  @Header("Content-Type") acceptFormat: String,
                                  @Path("id") id: Int?): Observable<ServerResponseWrapper<TesHarian>>

    @PUT("/tesharian/{id}")
    fun updateTesHarianObservable(@Header("Authorization") token: String,
                                  @Header("Content-Type") acceptFormat: String,
                                  @Path("id") id: Int?,
                                  @Body tesHarian: TesHarian): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/hasiltesharian/{id}/update")
    fun updateHasilTesHarian(@Header("Authorization") token: String,
                             @Header("Content-Type") acceptFormat: String,
                             @Path("id") id: Int?,
                             @Body tesHarian: HasilTesHarianRequest): Observable<ServerResponseWrapper<Boolean>>

    @GET("/siswa/all")
    fun loadAllSiswaObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Siswa>>>

    @POST("/siswa/create")
    fun addSiswaObservable(@Header("Authorization") token: String,
                           @Header("Content-Type") acceptFormat: String,
                           @Body siswa: SiswaRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/siswa/{id}/update")
    fun updateSiswaObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Path("id") id: String?,
                              @Body siswa: SiswaRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/siswa/{id}/updatestatus")
    fun updateStatusSiswaObservable(@Header("Authorization") token: String,
                                    @Header("Content-Type") acceptFormat: String,
                                    @Path("id") id: String?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/bidang/all")
    fun loadAllBidangObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Bidang>>>

    @POST("/bidang/create")
    fun createBidangObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String,
                               @Body bidang: BidangRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/bidang/{nama}/delete")
    fun deleteBidangObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String,
                               @Path("nama") nama: String?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/kelas/all")
    fun loadAllKelasObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Kelas>>>

    @POST("/kelas/create")
    fun addKelasObservable(@Header("Authorization") token: String,
                           @Header("Content-Type") acceptFormat: String,
                           @Body kelas: KelasRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/kelas/{id}/update")
    fun updateKelasObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Path("id") id: Int?,
                              @Body kelas: KelasRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/kelas/{id}/updatestatus")
    fun updateStatusKelasObservable(@Header("Authorization") token: String,
                                    @Header("Content-Type") acceptFormat: String,
                                    @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/jadwalkelas/all")
    fun loadAllJadwalObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<JadwalKelas>>>

    @POST("/jadwalkelas/add")
    fun createJadwalObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String,
                               @Body jadwalKelas: JadwalRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/jadwalkelas/{id}/update")
    fun updateJadwalObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String,
                               @Path("id") id: Int?,
                               @Body jadwalKelas: JadwalRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/jadwalkelas/{id}/delete")
    fun deleteJadwalObservable(@Header("Authorization") token: String,
                               @Header("Content-Type") acceptFormat: String,
                               @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/jadwalkelas/user/{idJadwal}")
    fun loadJadwalUserObservable(@Header("Authorization") token: String,
                                 @Header("Content-Type") acceptFormat: String,
                                 @Path("idJadwal") id: Int?): Observable<ServerResponseWrapper<User>>

    @GET("/sekolah/all")
    fun loadAllSekolahObservable(@Header("Authorization") token: String,
                                 @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Sekolah>>>

    @POST("/sekolah/create")
    fun createSekolahObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Body sekolah: SekolahRequestWrapper): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/sekolah/{nama}/delete")
    fun deleteSekolahObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Path("nama") nama: String?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/kegiatan/{id}")
    fun loadJournalObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Path("id") id: Int?): Observable<ServerResponseWrapper<MutableList<Kegiatan>>>

    @POST("/kegiatan/add")
    fun createJournalObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Body kegiatan: KegiatanRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/kegiatan/{id}/update")
    fun updateJournalObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Path("id") id: Int?,
                                @Body kegiatan: KegiatanRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/kegiatan/{id}/delete")
    fun deleteJournalObservable(@Header("Authorization") token: String,
                                @Header("Content-Type") acceptFormat: String,
                                @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/pengeluaran/all")
    fun loadAllPengeluaranObservable(@Header("Authorization") token: String,
                                     @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Pengeluaran>>>

    @GET("/pengeluaran/{id}")
    fun loadPengeluaranObservable(@Header("Authorization") token: String,
                                  @Header("Content-Type") acceptFormat: String,
                                  @Path("id") id: Int?): Observable<ServerResponseWrapper<MutableList<Pengeluaran>>>

    @POST("/pengeluaran/add")
    fun createPengeluaranObservable(@Header("Authorization") token: String,
                                    @Header("Content-Type") acceptFormat: String,
                                    @Body pengeluaran: PengeluaranRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/pengeluaran/{id}/update")
    fun updatePengeluaranObservable(@Header("Authorization") token: String,
                                    @Header("Content-Type") acceptFormat: String,
                                    @Path("id") id: Int?,
                                    @Body pengeluaran: PengeluaranRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/pengeluaran/{id}/delete")
    fun deletePengeluaranObservable(@Header("Authorization") token: String,
                                    @Header("Content-Type") acceptFormat: String,
                                    @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

    @GET("/event/all")
    fun loadEventObservable(@Header("Authorization") token: String,
                            @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<Event>>>

    @GET("/event/{id}")
    fun loadEventObservable(@Header("Authorization") token: String,
                            @Header("Content-Type") acceptFormat: String,
                            @Path("id") id: Int?): Observable<ServerResponseWrapper<Event>>

    @POST("/event/create")
    fun createEventObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Body pengeluaran: EventRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @PUT("/event/{id}/update")
    fun updateEventObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Path("id") id: Int?,
                              @Body pengeluaran: EventRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>

    @DELETE("/event/{id}/delete")
    fun deleteEventObservable(@Header("Authorization") token: String,
                              @Header("Content-Type") acceptFormat: String,
                              @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

//    @GET("/bidanguser/all")
//    fun loadBidangUserObservable(@Header("Authorization") token: String,
//                                 @Header("Content-Type") acceptFormat: String): Observable<ServerResponseWrapper<MutableList<BidangUser>>>
//
//    @GET("/bidanguser/{email}")
//    fun loadBidangUserObservable(@Header("Authorization") token: String,
//                                 @Header("Content-Type") acceptFormat: String,
//                                 @Path("email") email: String?): Observable<ServerResponseWrapper<MutableList<BidangUser>>>
//
//    @POST("/bidanguser/create")
//    fun createBidangUserObservable(@Header("Authorization") token: String,
//                                   @Header("Content-Type") acceptFormat: String,
//                                   @Body bidanguser: BidangUserRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>
//
//    @PUT("/bidanguser/{id}/update")
//    fun updateBidangUserObservable(@Header("Authorization") token: String,
//                                   @Header("Content-Type") acceptFormat: String,
//                                   @Path("id") id: Int?,
//                                   @Body bidanguser: BidangUserRequestWrapper?): Observable<ServerResponseWrapper<Boolean>>
//
//    @DELETE("/bidanguser/{id}/delete")
//    fun deleteBidangUserObservable(@Header("Authorization") token: String,
//                                   @Header("Content-Type") acceptFormat: String,
//                                   @Path("id") id: Int?): Observable<ServerResponseWrapper<Boolean>>

}