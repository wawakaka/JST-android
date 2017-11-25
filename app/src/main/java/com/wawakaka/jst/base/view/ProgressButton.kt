package com.wawakaka.jst.base.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.wawakaka.jst.R
import kotlinx.android.synthetic.main.progress_button.view.*
import kotlinx.android.synthetic.main.view_loading.view.*
import org.jetbrains.anko.backgroundDrawable

/**
 * Created by wawakaka on 10/11/2017.
 */
class ProgressButton : LinearLayout {

    companion object {
        private const val PROGRESS_COLOR_LIGHT = 0
        private const val PROGRESS_COLOR_DARK = 1
    }

    private var text: String = ""
    private var textColor: Int = R.color.black
    private var progressColor: Int = R.color.white
    private var buttonBackground: Drawable? = null
    private var image: Drawable? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
        initView()
    }

    fun init(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ProgressButton, 0, 0
        )

        text = typedArray.getString(R.styleable.ProgressButton_buttonText)
        buttonBackground = typedArray.getDrawable(R.styleable.ProgressButton_buttonBackground)
        textColor = typedArray.getColor(R.styleable.ProgressButton_buttonTextColor, R.color.black)
        progressColor = typedArray.getInt(R.styleable.ProgressButton_progressColor, PROGRESS_COLOR_LIGHT)
        image = typedArray.getDrawable(R.styleable.ProgressButton_buttonImage)

        typedArray?.recycle()
    }

    private fun initView() {
        val layoutInflater = ViewUtils.getLayoutInflater(context)
        layoutInflater?.inflate(R.layout.progress_button, this, true)

        gravity = Gravity.CENTER
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!isInEditMode) {
            initElements()
        }
    }

    private fun initElements() {
        gravity = Gravity.CENTER
        backgroundDrawable = buttonBackground
        button_text.text = text
        button_text.setTextColor(textColor)

        if (image != null) {
            button_image.makeVisible()
            button_image.setImageDrawable(image)
        }

        when (progressColor) {
            PROGRESS_COLOR_LIGHT -> Glide
                .with(context)
                .asGif()
                .load(R.raw.loading)
                .into(progress_bar.loading_icon)
            PROGRESS_COLOR_DARK -> Glide
                .with(context)
                .asGif()
                .load(R.raw.loading_white)
                .into(progress_bar.loading_icon)
        }
    }

    fun setButtonText(text: String) {
        button_text.text = text
    }

    fun setButtonTextColor(color: Int) {
        textColor = color
        button_text.setTextColor(color)
    }

    fun enableLoadingState() {
        button_text.text = ""
        button_text.makeGone()
        button_image.makeGone()
        progress_bar.makeVisible()
        isEnabled = false
    }

    fun disableLoadingState() {
        button_text.text = text
        button_text.makeVisible()
        if (image != null) button_image.makeVisible()
        progress_bar.makeGone()
        isEnabled = true
    }

    fun setEnabledState(enabled: Boolean) {
        isEnabled = enabled
        button_text.isEnabled = enabled
    }

}