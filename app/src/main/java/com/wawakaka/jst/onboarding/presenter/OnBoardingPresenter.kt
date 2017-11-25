package com.wawakaka.jst.onboarding.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 11/13/2017.
 */
class OnBoardingPresenter(private val serverRequestManager: ServerRequestManager,
                          private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = OnBoardingPresenter::class.java.simpleName
    }

    fun updateUserObservable(user: User): Observable<User> {
        return serverRequestManager
            .updateUserObservable(user)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() ?: true }
            .map { it.data!! }
            .doOnNext { saveUser(it) }
    }

    fun getUser(): User {
        return localRequestManager.getUser()
    }

    private fun saveUser(user: User) {
        localRequestManager.saveUser(user)
    }


}