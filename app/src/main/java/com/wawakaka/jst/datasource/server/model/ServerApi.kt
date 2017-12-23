package com.wawakaka.jst.datasource.server.model

import com.wawakaka.jst.dailytest.model.TesHarian
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.presensi.model.Presensi
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by wawakaka on 7/25/2017.
 */
interface ServerApi {

    @POST("user/login/{email}")
    fun loginObservable(@Header("Content-Type") acceptFormat: String,
                        @Path("email") email: String?,
                        @Body user: User): Observable<ServerResponseWrapper<User>>

    @PUT("user/{email}")
    fun updateUserObservable(@Header("Content-Type") acceptFormat: String,
                             @Path("email") email: String?,
                             @Body user: User): Observable<ServerResponseWrapper<User>>

    @GET("kelas/{email}")
    fun loadClassObservable(@Header("Content-Type") acceptFormat: String,
                            @Path("email") email: String?): Observable<ServerResponseWrapper<MutableList<Kelas>>>

    @GET("/presensi/{id}")
    fun loadPresensiCheckedListObservable(@Header("Content-Type") acceptFormat: String,
                                          @Path("id") id: Int?): Observable<ServerResponseWrapper<MutableList<Presensi>>>

    @POST("/all/{id}")
    fun savePresensiCheckedListObservable(@Header("Content-Type") acceptFormat: String,
                                          @Path("id") id: Int?,
                                          @Body presensi: MutableList<Presensi>): Observable<ServerResponseWrapper<Boolean>>

    @GET("/tesharian/{id}")
    fun loadTesHarianObservable(@Header("Content-Type") acceptFormat: String,
                                @Path("id") id: Int?): Observable<ServerResponseWrapper<TesHarian>>

    @POST("/tesharian/{id}")
    fun updateTesHarianObservable(@Header("Content-Type") acceptFormat: String,
                                  @Path("id") id: Int?,
                                  @Body tesHarian: TesHarian): Observable<ServerResponseWrapper<Boolean>>
}