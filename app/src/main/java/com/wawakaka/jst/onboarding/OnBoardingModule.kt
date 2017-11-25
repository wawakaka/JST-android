package com.wawakaka.jst.onboarding

import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.onboarding.presenter.OnBoardingPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/13/2017.
 */
@Module
class OnBoardingModule {

    @Provides
    @Singleton
    fun provideOnBoardingPresenter(serverRequestManager: ServerRequestManager,
                                   localRequestManager: LocalRequestManager): OnBoardingPresenter {
        return OnBoardingPresenter(
            serverRequestManager,
            localRequestManager
        )
    }
}