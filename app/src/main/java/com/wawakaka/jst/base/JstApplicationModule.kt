package com.wawakaka.jst.base

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 7/25/2017.
 */
@Module
class JstApplicationModule(private val jstApplication: JstApplication) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return jstApplication
    }

}