package com.wawakaka.jst.datasource.local

import com.wawakaka.jst.datasource.local.preference.PreferenceApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/6/2017.
 */
@Module
class LocalRequestModule {

    @Singleton
    @Provides
    fun provideLocalRequestManager(preferenceApi: PreferenceApi): LocalRequestManager {
        return LocalRequestManager(preferenceApi)
    }

}