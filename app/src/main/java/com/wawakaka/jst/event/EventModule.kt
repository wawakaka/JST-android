package com.wawakaka.jst.event

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.event.presenter.EventPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class EventModule {

    @Provides
    @Singleton
    fun provideEventPresenter(serverRequestManager: ServerRequestManager,
                              localRequestManager: LocalRequestManager): EventPresenter {
        return EventPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}