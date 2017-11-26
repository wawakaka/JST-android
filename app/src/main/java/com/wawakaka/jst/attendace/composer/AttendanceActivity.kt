package com.wawakaka.jst.attendace.composer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wawakaka.jst.R

class AttendanceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS = "extra_kelas"
        const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
    }
}
