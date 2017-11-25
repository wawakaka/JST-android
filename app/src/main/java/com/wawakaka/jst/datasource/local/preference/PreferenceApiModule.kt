package com.wawakaka.jst.datasource.local.preference

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/6/2017.
 */
@Module
class PreferenceApiModule {

    @Singleton
    @Provides
    fun providePreferenceApi(context: Context): PreferenceApi {
        return PreferenceApi(context)
    }

}