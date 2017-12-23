package com.wawakaka.jst.dailytest.composer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wawakaka.jst.R

class TestResultAddOrEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DAILY_TEST_ID = "daily-test-id"
        const val EXTRA_DAILY_TEST_RESULT = "daily-test-result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result_add_or_edit)
    }
}
