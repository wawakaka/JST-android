package com.wawakaka.jst.camera

import com.wawakaka.jst.camera.presenter.CameraPresenter
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
    fun provideCameraPresenter(): CameraPresenter {
        return CameraPresenter()
    }

}