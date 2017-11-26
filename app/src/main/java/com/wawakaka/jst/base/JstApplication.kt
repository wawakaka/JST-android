package com.wawakaka.jst.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.wawakaka.jst.BuildConfig
import com.wawakaka.jst.R
import com.wawakaka.jst.base.utils.LogUtils
import net.danlew.android.joda.JodaTimeAndroid

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
        initJodaTime()
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

    private fun initJodaTime() {
        JodaTimeAndroid.init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
