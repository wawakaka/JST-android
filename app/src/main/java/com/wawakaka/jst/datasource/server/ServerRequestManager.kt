package com.wawakaka.jst.datasource.server

import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.server.model.*
import com.wawakaka.jst.datasource.server.utils.isServerRequestErrorNetwork
import com.wawakaka.jst.datasource.server.utils.isServerRequestErrorNoInternet
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.presensi.model.PresensiRequestWrapper
import com.wawakaka.jst.tesHarian.model.TesHarian
import com.wawakaka.jst.tesHarian.model.TesHarianRequest
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by wawakaka on 7/25/2017.
 */
class ServerRequestManager(private val serverApi: ServerApi) {

    companion object {
        val TAG: String = ServerRequestManager::class.java.simpleName
    }

    fun loginObservable(user: User): Observable<ServerResponseWrapper<User>> {
        return handleServerRequestError(
            serverApi.loginObservable(
                getAcceptApplicationJsonHeader(),
                user.email,
                user
            )
        )
    }

    fun updateUserObservable(user: User): Observable<ServerResponseWrapper<User>> {
        return handleServerRequestError(
            serverApi.updateUserObservable(
                getAcceptApplicationJsonHeader(),
                user.email,
                user
            )
        )
    }

    fun loadClassObservable(user: User): Observable<ServerResponseWrapper<MutableList<Kelas>>> {
        return handleServerRequestError(
            serverApi.loadClassObservable(
                getAcceptApplicationJsonHeader(),
                user.email
            )
        )
    }

    fun loadPresensiCheckedListObservable(idJadwalKelas: Int?): Observable<ServerResponseWrapper<MutableList<Presensi>>> {
        return handleServerRequestError(
            serverApi.loadPresensiCheckedListObservable(
                getAcceptApplicationJsonHeader(),
                idJadwalKelas
            )
        )
    }

    fun savePresensiCheckedListObservable(jadwalKelasId: Int?,
                                          request: PresensiRequestWrapper): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.savePresensiCheckedListObservable(
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
                getAcceptApplicationJsonHeader(),
                idJadwalKelas,
                tesHarian
            )
        )
    }

    fun updateTesHarianObservable(tesHarian: TesHarian?): Observable<ServerResponseWrapper<Boolean>> {
        return handleServerRequestError(
            serverApi.updateTesHarianObservable(
                getAcceptApplicationJsonHeader(),
                tesHarian!!.id,
                tesHarian
            )
        )
    }

    fun loadSiswaObservable(): Observable<ServerResponseWrapper<MutableList<Siswa>>> {
        return handleServerRequestError(
            serverApi.loadSiswaObservable(
                getAcceptApplicationJsonHeader()
            )
        )
    }

    /**
     * Handle error occurred from original observable.
     *
     * @param originalObservable Observable original that will be executed immediately
     */
    private fun <T> handleServerRequestError(originalObservable: Observable<T>): Observable<T> {
        return originalObservable.onErrorResumeNext(Function<Throwable, Observable<T>> {
            if (it.isServerRequestErrorNoInternet()) {
                Observable.error(NoInternetError(it))
            } else if (it.isServerRequestErrorNetwork()) {
                Observable.error(NetworkError(it))
            } else {
                Observable.error(UnknownError(it))
            }
        })
    }

    private fun getAcceptApplicationJsonHeader(): String {
        return "application/json"
    }

}