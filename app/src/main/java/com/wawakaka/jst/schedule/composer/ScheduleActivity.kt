package com.wawakaka.jst.schedule.composer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wawakaka.jst.R

class ScheduleActivity : AppCompatActivity() {

    companion object {
        const val KELAS_INFO = "schedule_info"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
    }
}
