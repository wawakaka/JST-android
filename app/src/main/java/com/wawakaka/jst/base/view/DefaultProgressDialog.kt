package com.wawakaka.jst.base.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.wawakaka.jst.R
import kotlinx.android.synthetic.main.dialog_progress_default.*

/**
 * Created by wawakaka on 11/13/2017.
 */
class DefaultProgressDialog(context: Context,
                            val message: String,
                            private val cancelable: Boolean) : NaviDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLayout()
    }

    private fun initLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress_default)

        window.setBackgroundDrawableResource(R.color.transparent)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setCancelable(cancelable)
        setDialogMessage()
    }

    private fun setDialogMessage() {
        dialog_message.text = message
    }


}