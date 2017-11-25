package com.wawakaka.jst.navigation.presenter

import com.birbit.android.jobqueue.JobManager
import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.login.presenter.LoginPresenter
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created by wawakaka on 11/24/2017.
 */
class NavigationPresenter(private val localRequestManager: LocalRequestManager,
                          private val loginPresenter: LoginPresenter,
                          private val jobManager: JobManager) : BasePresenter() {

    fun onLogoutConfirmedObservable(): Observable<Boolean> {
        return Observable
            .just(true)
            .observeOn(Schedulers.io())
            .doOnNext {
                clearSaveData()
                cancelAllOngoingJobs()
                loginPresenter.getFireBaseAuth().signOut()
            }
    }

    override fun takeLocalRequestManager(): LocalRequestManager? = localRequestManager

    override fun takeJobManager(): JobManager? = jobManager
}