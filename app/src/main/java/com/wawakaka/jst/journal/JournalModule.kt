package com.wawakaka.jst.journal

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.journal.presenter.JournalPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by babang on 1/28/2018.
 */
@Module
class JournalModule {

    @Provides
    @Singleton
    fun provideJournalPresenter(serverRequestManager: ServerRequestManager,
                                localRequestManager: LocalRequestManager): JournalPresenter {
        return JournalPresenter(
            serverRequestManager,
            localRequestManager
        )
    }

}