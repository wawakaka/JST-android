package com.wawakaka.jst.datasource.server.model

import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.login.model.User
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
}