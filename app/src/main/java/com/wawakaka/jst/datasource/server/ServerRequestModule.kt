package com.wawakaka.jst.datasource.server

import com.wawakaka.jst.datasource.server.model.ServerApi
import dagger.Module
import dagger.Provides

/**
 * Created by wawakaka on 7/25/2017.
 */
@Module
class ServerRequestModule {

    @Provides
    fun provideServerRequestManager(serverApi: ServerApi): ServerRequestManager {
        return ServerRequestManager(serverApi)
    }
}