package com.wawakaka.jst.base.view

import android.content.Context
import android.graphics.Typeface
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import com.wawakaka.jst.R

/**
 * Created by wawakaka on 12/25/2017.
 */
open class DefaultTextInputEditText : TextInputEditText {
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