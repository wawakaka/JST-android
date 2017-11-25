package com.wawakaka.jst.base.utils

import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by wawakaka on 7/26/2017.
 */
@Module
class LogUtilsModule {

    @Provides
    @Named("log_enabled")
    fun provideLogEnabled(): Boolean {
        return LogUtils.logEnabled
    }

}