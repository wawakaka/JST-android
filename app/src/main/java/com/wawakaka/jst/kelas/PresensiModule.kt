package com.wawakaka.jst.kelas

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.presensi.presenter.PresensiPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/27/2017.
 */
@Module
class PresensiModule {

    @Provides
    @Singleton
    fun providePresensiPresenter(serverRequestManager: ServerRequestManager,
                                 localRequestManager: LocalRequestManager): PresensiPresenter {
        return PresensiPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}