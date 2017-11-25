package com.wawakaka.jst.navigation

import com.birbit.android.jobqueue.JobManager
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.login.presenter.LoginPresenter
import com.wawakaka.jst.navigation.presenter.NavigationPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/24/2017.
 */
@Module
class NavigationModule {

    @Provides
    @Singleton
    fun provideNavigationPresenter(localRequestManager: LocalRequestManager,
                                   loginPresenter: LoginPresenter,
                                   jobManager: JobManager): NavigationPresenter {
        return NavigationPresenter(
            localRequestManager,
            loginPresenter,
            jobManager
        )
    }
}