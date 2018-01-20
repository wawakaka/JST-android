package com.wawakaka.jst.base.view

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.wawakaka.jst.R

/**
 * Created by wawakaka on 12/31/2017.
 */
class DefaultButton : AppCompatButton {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        val defaultFont = Typeface.createFromAsset(context.assets, context.getString(R.string.font_default))
        typeface = defaultFont
    }
}