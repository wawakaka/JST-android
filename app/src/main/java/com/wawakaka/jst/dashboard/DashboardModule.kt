package com.wawakaka.jst.dashboard

import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/24/2017.
 */
@Module
class DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardPresenter(serverRequestManager: ServerRequestManager,
                                  localRequestManager: LocalRequestManager): DashboardPresenter {
        return DashboardPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}