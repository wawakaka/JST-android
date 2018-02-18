package com.wawakaka.jst.camera.composer

import android.os.Bundle
import android.widget.ArrayAdapter
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wonderkiln.camerakit.CameraKit
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by wawakaka on 10/11/2017.
 */
class CameraActivity : BaseActivity() {
    //    //todo try to do camera app
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camera)
//    }
//
//    init {
//        initLayout()
//        initOnResume()
//        initOnPause()
//        initCaptureImage()
//    }
//
//    private fun initLayout() {
//        RxNavi
//            .observe(naviComponent, Event.CREATE)
//            .observeOn(AndroidSchedulers.mainThread())
//            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
//            .subscribe {
//                setContentView(R.layout.activity_camera)
//                camera.start()
//            }
//    }
//
//    private fun initOnResume() {
//        RxNavi
//            .observe(naviComponent, Event.RESUME)
//            .observeOn(AndroidSchedulers.mainThread())
//            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
//            .subscribe {
//                super.onResume()
//                camera.start()
//            }
//    }
//
//    private fun initOnPause() {
//        RxNavi
//            .observe(naviComponent, Event.PAUSE)
//            .observeOn(AndroidSchedulers.mainThread())
//            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
//            .subscribe {
//                camera.stop()
//                super.onPause()
//            }
//    }
//
//    private fun initCaptureImage(){
//        RxNavi
//            .observe(naviComponent, Event.PAUSE)
//            .observeOn(AndroidSchedulers.mainThread())
//            .flatMap { RxView.clicks(capture) }
//            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
//            .subscribe {
////                camera.captureImage(CameraKitEventCallback { t ->  })
//
////                camera.captureImage(CameraKitEventCallback<CameraKitImage> { event -> imageCaptured(event) })
//            }
//    }
    companion object {
        private val TAG = CameraActivity::class.java!!.getSimpleName()

    }

    private var drawerAdapter: ArrayAdapter<*>? = null

    private var cameraMethod = CameraKit.Constants.METHOD_STANDARD
    private var cropOutput = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        camera!!.setMethod(cameraMethod)
        camera!!.setCropOutput(cropOutput)
    }

    override fun onResume() {
        super.onResume()
        camera!!.start()
    }

    override fun onPause() {
        camera!!.stop()
        super.onPause()
    }

    private abstract class CameraSetting {

        internal abstract val title: String?
        internal abstract val value: String?
        internal abstract fun toggle()

    }

    private val captureMethodSetting = object : CameraSetting() {
        override val title: String
            get() = "ckMethod"

        override val value: String?
            get() {
                return when (cameraMethod) {
                    CameraKit.Constants.METHOD_STANDARD -> {
                        "standard"
                    }

                    CameraKit.Constants.METHOD_STILL -> {
                        "still"
                    }

                    else -> null
                }
            }

        override fun toggle() {
            cameraMethod = if (cameraMethod == CameraKit.Constants.METHOD_STANDARD) {
                CameraKit.Constants.METHOD_STILL
            } else {
                CameraKit.Constants.METHOD_STANDARD
            }

            camera!!.setMethod(cameraMethod)
        }
    }

    private val cropSetting = object : CameraSetting() {
        override val title: String
            get() = "ckCropOutput"

        override val value: String
            get() = if (cropOutput) {
                "true"
            } else {
                "false"
            }

        override fun toggle() {
            cropOutput = !cropOutput
            camera!!.setCropOutput(cropOutput)
        }
    }
}
