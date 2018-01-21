package com.wawakaka.jst.datasource.server.model

import com.wawakaka.jst.admin.model.*
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.login.model.User
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
                        @Body user: User): Observable<ServerResponseWrapper<User>>

    @PUT("user/{email}")
    fun updateUserObservable(@Header("Authorization") token: String,
                             @Header("Content-Type") acceptFormat: String,
                             @Path("email") email: String?,
                             @Body user: User): Observable<ServerResponseWrapper<User>>

    @GET("kelas/{email}")
    fun loadKelasObservable(@Header("Authorization") token: String,
                            @Header("Content-Type") acceptFormat: String,
                            @Path("email") email: String?): Observable<ServerResponseWrapper<MutableList<Kelas>>>

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
}