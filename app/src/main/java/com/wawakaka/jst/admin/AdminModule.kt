package com.wawakaka.jst.admin

import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 12/29/2017.
 */
@Module
class AdminModule {

    @Provides
    @Singleton
    fun provideAdminPresenter(serverRequestManager: ServerRequestManager,
                              localRequestManager: LocalRequestManager): AdminPresenter {
        return AdminPresenter(
                serverRequestManager,
                localRequestManager
        )
    }
}