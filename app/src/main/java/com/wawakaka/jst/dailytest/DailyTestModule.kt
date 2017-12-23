package com.wawakaka.jst.dailytest

import com.wawakaka.jst.dailytest.presenter.DailyTestPresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/27/2017.
 */
@Module
class DailyTestModule {

    @Provides
    @Singleton
    fun provideDailyTestPresenter(serverRequestManager: ServerRequestManager,
                                  localRequestManager: LocalRequestManager): DailyTestPresenter {
        return DailyTestPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}