package com.wawakaka.jst.base

import android.content.Context
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wawakaka on 11/24/2017.
 */
@Module
class JobModule {

    //todo just remove job if you doesn't need it

    companion object {
        private val ID = JobModule::class.java.simpleName
    }

    @Singleton
    @Provides
    fun provideJobManager(context: Context): JobManager {
        val configuration = Configuration.Builder(context)
            .id(ID)
            .resetDelaysOnRestart()
            .build()
        return JobManager(configuration)
    }

}