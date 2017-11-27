package com.wawakaka.jst.dailytest.composer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wawakaka.jst.R

class DailyTestActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS = "extra_kelas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_test)
    }
}
