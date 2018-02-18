package com.wawakaka.jst.camera

import com.wawakaka.jst.camera.presenter.CameraPresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 1/31/2018.
 */
@Module
class CameraModule {

    @Provides
    @Singleton
    fun provideCameraPresenter(serverRequestManager: ServerRequestManager,
                               localRequestManager: LocalRequestManager): CameraPresenter {
        return CameraPresenter(serverRequestManager, localRequestManager)
    }

}