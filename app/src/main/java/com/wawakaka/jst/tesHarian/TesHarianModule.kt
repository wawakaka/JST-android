package com.wawakaka.jst.tesHarian

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.tesHarian.presenter.TesHarianPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/27/2017.
 */
@Module
class TesHarianModule {

    @Provides
    @Singleton
    fun provideDailyTestPresenter(serverRequestManager: ServerRequestManager,
                                  localRequestManager: LocalRequestManager): TesHarianPresenter {
        return TesHarianPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}