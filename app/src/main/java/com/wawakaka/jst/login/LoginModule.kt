package com.wawakaka.jst.login

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.presenter.LoginPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 10/12/2017.
 */
@Module
class LoginModule {

    @Provides
    @Singleton
    fun provideLoginPresenter(serverRequestManager: ServerRequestManager,
                              localRequestManager: LocalRequestManager): LoginPresenter {
        return LoginPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}