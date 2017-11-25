package com.wawakaka.jst.base.view

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.wawakaka.jst.R
import kotlinx.android.synthetic.main.view_loading.view.*

/**
 * Created by wawakaka on 10/11/2017.
 */
class LoadingView : FrameLayout {

    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initViews()
    }

    private fun initViews() {
        val layoutInflater = ViewUtils.getLayoutInflater(context)
        layoutInflater?.inflate(R.layout.view_loading, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        showLoadingIcon()
    }

    private fun showLoadingIcon() {
        if (!isInEditMode) {
            Glide
                .with(context)
                .asGif()
                .load(R.raw.loading)
                .into(loading_icon)
        }
    }

}
