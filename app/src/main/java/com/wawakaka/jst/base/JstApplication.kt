package com.wawakaka.jst.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.cloudinary.android.MediaManager
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
        private const val CLOUD_NAME = "cloud_name"
        private const val API_KEY = "api_key"
        private const val API_SECRET = "api_secret"
    }

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initDagger()
        initJodaTime()
        initCdn()
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

    private fun initCdn() {
        val config = HashMap<String, String>()
        config[CLOUD_NAME] = BuildConfig.CDN_CLOUD_NAME
        config[API_KEY] = BuildConfig.CDN_API_KEY
        config[API_SECRET] = BuildConfig.CDN_API_SECRET
        MediaManager.init(this, config)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
