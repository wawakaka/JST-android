package com.wawakaka.jst.dashboard.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.KelasListRefreshEvent
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/13/2017.
 */
class DashboardPresenter(private val serverRequestManager: ServerRequestManager,
                         private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = DashboardPresenter::class.java.simpleName
    }

    fun loadClassObservable(): Observable<MutableList<Kelas>> {
        return serverRequestManager
            .loadClassObservable(getUser())
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() ?: true }
            .map { it.data?.sortedBy { it.id }?.toMutableList() ?: mutableListOf() }
            .doOnNext { saveKelas(it) }
    }

    private fun getUser(): User = localRequestManager.getUser()

    private fun saveKelas(listKelas: List<Kelas>) {
        localRequestManager.saveKelas(listKelas)
    }

    fun listenRefreshListKelasEvent() = RxBus.registerObservable<KelasListRefreshEvent>()

    fun publishRefreshListKelasEvent() {
        RxBus.post(KelasListRefreshEvent())
    }


}