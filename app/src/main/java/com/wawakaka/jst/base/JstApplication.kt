package com.wawakaka.jst.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.wawakaka.jst.BuildConfig
import com.wawakaka.jst.R
import com.wawakaka.jst.base.utils.LogUtils

/**
 * Created by wawakaka on 7/25/2017.
 */
class JstApplication : Application() {

    companion object {
        lateinit var component: JstComponent
    }

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initDagger()
    }

    private fun initDagger() {
        component = DaggerJstComponent.builder()
            .jstApplicationModule(JstApplicationModule(this))
            .build()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            LogUtils.enableLogging(true, getString(R.string.app_name))
        }
        Log.d("init logging", "")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
