//package com.wawakaka.jst.camera.view
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.content.Context
//import android.content.Intent
//import android.content.res.TypedArray
//import android.support.annotation.DrawableRes
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.animation.OvershootInterpolator
//import android.widget.ImageView
//import android.widget.LinearLayout
//
//import com.wawakaka.jst.camera.view.CameraControls
//import com.wonderkiln.camerakit.CameraKit
//import com.wonderkiln.camerakit.CameraKitEventCallback
//import com.wonderkiln.camerakit.CameraKitImage
//import com.wonderkiln.camerakit.CameraKitVideo
//import com.wonderkiln.camerakit.CameraView
//import com.wonderkiln.camerakit.OnCameraKitEvent
//
//import java.io.File
//
//import butterknife.BindView
//import butterknife.ButterKnife
//import butterknife.OnTouch
//import com.wawakaka.jst.R
//import kotlinx.android.synthetic.main.camera_controls.view.*
//
//class CameraControls @JvmOverloads constructor(context: Context,
//                                               attrs: AttributeSet? = null,
//                                               defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
//
//    private var cameraViewId = -1
//    private var cameraView: CameraView? = null
//
//    private var coverViewId = -1
//    private var coverView: View? = null
//
//    private var captureDownTime: Long = 0
//    private var captureStartTime: Long = 0
//    private var pendingVideoCapture: Boolean = false
//    private var capturingVideo: Boolean = false
//
//    init {
//        LayoutInflater.from(getContext()).inflate(R.layout.camera_controls, this)
//
//        if (attrs != null) {
//            val a = context.theme.obtainStyledAttributes(
//                attrs,
//                R.styleable.CameraControls,
//                0, 0)
//
//            try {
//                cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1)
//                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1)
//            } finally {
//                a.recycle()
//            }
//        }
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        if (cameraViewId != -1) {
//            val view = rootView.findViewById<View>(cameraViewId)
//            if (view is CameraView) {
//                cameraView = view
//                cameraView!!.bindCameraKitListener(this)
//                setFacingImageBasedOnCamera()
//            }
//        }
//
//        if (coverViewId != -1) {
//            val view = rootView.findViewById<View>(coverViewId)
//            if (view != null) {
//                coverView = view
//                coverView!!.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun setFacingImageBasedOnCamera() {
//        if (cameraView!!.isFacingFront) {
//            facingButton!!.setImageResource(R.drawable.ic_facing_back)
//        } else {
//            facingButton!!.setImageResource(R.drawable.ic_facing_front)
//        }
//    }
//
//    //@OnCameraKitEvent(CameraKitImage.class)
//    fun imageCaptured(image: CameraKitImage) {
//        val jpeg = image.jpeg
//
//        val callbackTime = System.currentTimeMillis()
//        ResultHolder.dispose()
//        ResultHolder.image = jpeg
//        ResultHolder.nativeCaptureSize = cameraView!!.captureSize
//        ResultHolder.timeToCallback = callbackTime - captureStartTime
//        val intent = Intent(context, PreviewActivity::class.java)
//        context.startActivity(intent)
//    }
//
//    @OnCameraKitEvent(CameraKitVideo::class)
//    fun videoCaptured(video: CameraKitVideo) {
//        val videoFile = video.videoFile
//        if (videoFile != null) {
//            ResultHolder.dispose()
//            ResultHolder.video = videoFile
//            ResultHolder.nativeCaptureSize = cameraView!!.captureSize
//            val intent = Intent(context, PreviewActivity::class.java)
//            context.startActivity(intent)
//        }
//    }
//
//    @OnTouch(R.id.captureButton)
//    internal fun onTouchCapture(view: View, motionEvent: MotionEvent): Boolean {
//        handleViewTouchFeedback(view, motionEvent)
//        when (motionEvent.action) {
//            MotionEvent.ACTION_DOWN -> {
//                captureDownTime = System.currentTimeMillis()
//                pendingVideoCapture = true
//                postDelayed({
//                    if (pendingVideoCapture) {
//                        capturingVideo = true
//                        cameraView!!.captureVideo()
//                    }
//                }, 250)
//            }
//
//            MotionEvent.ACTION_UP -> {
//                pendingVideoCapture = false
//
//                if (capturingVideo) {
//                    capturingVideo = false
//                    cameraView!!.stopVideo()
//                } else {
//                    captureStartTime = System.currentTimeMillis()
//                    cameraView!!.captureImage { event -> imageCaptured(event) }
//                }
//            }
//        }
//        return true
//    }
//
//    @OnTouch(R.id.facingButton)
//    internal fun onTouchFacing(view: View, motionEvent: MotionEvent): Boolean {
//        handleViewTouchFeedback(view, motionEvent)
//        when (motionEvent.action) {
//            MotionEvent.ACTION_UP -> {
//                coverView!!.alpha = 0f
//                coverView!!.visibility = View.VISIBLE
//                coverView!!.animate()
//                    .alpha(1f)
//                    .setStartDelay(0)
//                    .setDuration(300)
//                    .setListener(object : AnimatorListenerAdapter() {
//                        override fun onAnimationEnd(animation: Animator) {
//                            super.onAnimationEnd(animation)
//                            if (cameraView!!.isFacingFront) {
//                                cameraView!!.facing = CameraKit.Constants.FACING_BACK
//                                changeViewImageResource(view as ImageView, R.drawable.ic_facing_front)
//                            } else {
//                                cameraView!!.facing = CameraKit.Constants.FACING_FRONT
//                                changeViewImageResource(view as ImageView, R.drawable.ic_facing_back)
//                            }
//
//                            coverView!!.animate()
//                                .alpha(0f)
//                                .setStartDelay(200)
//                                .setDuration(300)
//                                .setListener(object : AnimatorListenerAdapter() {
//                                    override fun onAnimationEnd(animation: Animator) {
//                                        super.onAnimationEnd(animation)
//                                        coverView!!.visibility = View.GONE
//                                    }
//                                })
//                                .start()
//                        }
//                    })
//                    .start()
//            }
//        }
//        return true
//    }
//
//    @OnTouch(R.id.flashButton)
//    internal fun onTouchFlash(view: View, motionEvent: MotionEvent): Boolean {
//        handleViewTouchFeedback(view, motionEvent)
//        when (motionEvent.action) {
//            MotionEvent.ACTION_UP -> {
//                if (cameraView!!.flash == CameraKit.Constants.FLASH_OFF) {
//                    cameraView!!.flash = CameraKit.Constants.FLASH_ON
//                    changeViewImageResource(view as ImageView, R.drawable.ic_flash_on)
//                } else {
//                    cameraView!!.flash = CameraKit.Constants.FLASH_OFF
//                    changeViewImageResource(view as ImageView, R.drawable.ic_flash_off)
//                }
//            }
//        }
//        return true
//    }
//
//    internal fun handleViewTouchFeedback(view: View, motionEvent: MotionEvent): Boolean {
//        when (motionEvent.action) {
//            MotionEvent.ACTION_DOWN -> {
//                touchDownAnimation(view)
//                return true
//            }
//
//            MotionEvent.ACTION_UP -> {
//                touchUpAnimation(view)
//                return true
//            }
//
//            else -> {
//                return true
//            }
//        }
//    }
//
//    internal fun touchDownAnimation(view: View) {
//        view.animate()
//            .scaleX(0.88f)
//            .scaleY(0.88f)
//            .setDuration(300)
//            .setInterpolator(OvershootInterpolator())
//            .start()
//    }
//
//    internal fun touchUpAnimation(view: View) {
//        view.animate()
//            .scaleX(1f)
//            .scaleY(1f)
//            .setDuration(300)
//            .setInterpolator(OvershootInterpolator())
//            .start()
//    }
//
//    internal fun changeViewImageResource(imageView: ImageView, @DrawableRes resId: Int) {
//        imageView.rotation = 0f
//        imageView.animate()
//            .rotationBy(360f)
//            .setDuration(400)
//            .setInterpolator(OvershootInterpolator())
//            .start()
//
//        imageView.postDelayed({ imageView.setImageResource(resId) }, 120)
//    }
//
//}