package com.wawakaka.jst.base.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.wawakaka.jst.R
import kotlinx.android.synthetic.main.view_error.view.*

/**
 * Created by wawakaka on 12/23/2017.
 */
class ErrorView : LinearLayout {

    companion object {

        // NOTE: Aspect ratio = width : height
        private val DEFAULT_ASPECT_RATIO = 1f
        private val DEFAULT_ASPECT_RATIO_ENABLED = false
        private val DEFAULT_DOMINANT_MEASUREMENT = AspectRatioImageView.MEASUREMENT_WIDTH
        private val DEFAULT_IMAGE_WIDTH = "imageWidth"
        private val DEFAULT_IMAGE_HEIGHT = "imageHeight"
        private val DEFAULT_ELEMENT_VERTICAL_MARGIN = 16
    }

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var imageAspectRatio: Float = 0.toFloat()
    private var imageAspectRatioEnabled: Boolean = false
    private var imageDominantMeasurement: Int = 0
    private var imageDrawable: Drawable? = null
    private var description: String? = null
    private var action: String? = ""
    private var elementVerticalMargin: Int = 0
    private var onClickListeners: OnClickListener? = null

    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
        initViews()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(attrs)
        initViews()
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ErrorView, 0, 0
        )

        imageWidth = typedArray.getLayoutDimension(
            R.styleable.ErrorView_imageWidth,
            DEFAULT_IMAGE_WIDTH
        )
        imageHeight = typedArray.getLayoutDimension(
            R.styleable.ErrorView_imageHeight,
            DEFAULT_IMAGE_HEIGHT
        )
        imageAspectRatio = typedArray.getFloat(
            R.styleable.ErrorView_imageAspectRatio,
            DEFAULT_ASPECT_RATIO
        )
        imageAspectRatioEnabled = typedArray.getBoolean(
            R.styleable.ErrorView_imageAspectRatioEnabled,
            DEFAULT_ASPECT_RATIO_ENABLED
        )
        imageDominantMeasurement = typedArray.getInt(
            R.styleable.ErrorView_imageDominantMeasurement,
            DEFAULT_DOMINANT_MEASUREMENT
        )
        imageDrawable = typedArray.getDrawable(R.styleable.ErrorView_imageDrawable)

        description = typedArray.getString(R.styleable.ErrorView_description)
        action = typedArray.getString(R.styleable.ErrorView_action)
        elementVerticalMargin = typedArray.getDimensionPixelSize(
            R.styleable.ErrorView_elementVerticalMargin,
            DEFAULT_ELEMENT_VERTICAL_MARGIN
        )

        typedArray.recycle()
    }

    private fun initViews() {
        val layoutInflater = ViewUtils.getLayoutInflater(context)
        layoutInflater?.inflate(R.layout.view_error, this, true)

        initImageView()
        initDescription(description)
        initAction(action, onClickListeners)
    }

    private fun initImageView() {
        error_image.layoutParams.width = imageWidth
        error_image.layoutParams.height = imageHeight
        error_image.setAspectRatio(imageAspectRatio)
        error_image.setAspectRatioEnabled(imageAspectRatioEnabled)
        error_image.setDominantMeasurement(imageDominantMeasurement)

        if (imageDrawable != null) {
            error_image.setImageDrawable(imageDrawable)
        }
    }

    private fun initDescription(description: String?) {
        if (!TextUtils.isEmpty(description)) {
            error_description.visibility = View.VISIBLE
            error_description.text = description
            (error_description.layoutParams as LayoutParams).topMargin = elementVerticalMargin
        } else {
            error_description.visibility = View.GONE
        }
    }

    private fun initAction(action: String?, onClickListener: OnClickListener?) {
        if (!TextUtils.isEmpty(action)) {
            action_button.visibility = View.VISIBLE
            action_button.text = action
            (action_button.layoutParams as LayoutParams).topMargin = elementVerticalMargin
            if (onClickListener != null) {
                setActionOnClickListener(onClickListener)
            }
        } else {
            action_button.visibility = View.GONE
        }
    }

    /**
     * Set error image width in pixel size. Can be one of the constants FILL_PARENT (replaced by
     * MATCH_PARENT in API Level 8) or WRAP_CONTENT, or an exact size.
     */
    fun setImageWidth(imageWidth: Int) {
        error_image.layoutParams.width = imageWidth
    }

    /**
     * Set error image height in pixel size. Can be one of the constants FILL_PARENT (replaced by
     * MATCH_PARENT in API Level 8) or WRAP_CONTENT, or an exact size.
     */
    fun setImageHeight(imageHeight: Int) {
        error_image.layoutParams.height = imageHeight
    }

    /**
     * Set the aspect ratio for the image. This will update the view instantly.
     */
    fun setImageAspectRatio(imageAspectRatio: Float) {
        error_image.setAspectRatio(imageAspectRatio)
    }

    /**
     * set whether or not forcing the aspect ratio of the image is enabled. This will re-layout the
     * view.
     */
    fun setImageAspectRatioEnabled(imageAspectRatioEnabled: Boolean) {
        error_image.setAspectRatioEnabled(imageAspectRatioEnabled)
    }

    /**
     * Set the dominant measurement for the aspect ratio of the image.

     * @see {@link AspectRatioImageView.MeasurementDef}
     */
    fun setImageDominantMeasurement(@AspectRatioImageView.MeasurementDef imageDominantMeasurement: Int) {
        error_image.setDominantMeasurement(imageDominantMeasurement)
    }

    /**
     * Set image drawable of error
     */
    fun setImageDrawable(drawable: Drawable) {
        imageDrawable = drawable
        error_image.setImageDrawable(drawable)
        requestLayout()
    }

    /**
     * Set description of error
     */
    fun setDescription(description: String) {
        this.description = description
        initDescription(description)
        requestLayout()
    }

    /**
     * Set action of error
     */
    fun setAction(action: String, onClickListener: OnClickListener) {
        this.action = action
        initAction(action, onClickListener)
        requestLayout()
    }

    /**
     * Set onClickListener of action
     */
    fun setActionOnClickListener(onClickListener: OnClickListener) {
        this.onClickListeners = onClickListener
        action_button.setOnClickListener(onClickListener)
    }

}
