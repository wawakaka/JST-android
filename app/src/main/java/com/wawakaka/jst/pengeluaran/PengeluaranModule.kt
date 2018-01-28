package com.wawakaka.jst.pengeluaran

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.pengeluaran.presenter.PengeluaranPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by babang on 1/28/2018.
 */
@Module
class PengeluaranModule {

    @Provides
    @Singleton
    fun providePengeluaranPresenter(serverRequestManager: ServerRequestManager,
                                    localRequestManager: LocalRequestManager): PengeluaranPresenter {
        return PengeluaranPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}