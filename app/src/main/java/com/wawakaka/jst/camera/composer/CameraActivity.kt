package com.wawakaka.jst.camera.composer

import android.os.Bundle
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wonderkiln.camerakit.CameraKitEventCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by wawakaka on 10/11/2017.
 */
class CameraActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    init {
        initLayout()
        initOnResume()
        initOnPause()
        initCaptureImage()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_camera)
            }
    }

    private fun initOnResume() {
        RxNavi
            .observe(naviComponent, Event.RESUME)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                super.onResume()
                camera.start()
            }
    }

    private fun initOnPause() {
        RxNavi
            .observe(naviComponent, Event.PAUSE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                camera.stop()
                super.onPause()
            }
    }

    private fun initCaptureImage(){
        RxNavi
            .observe(naviComponent, Event.PAUSE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(capture) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                camera.captureImage(CameraKitEventCallback { t ->  })

//                camera.captureImage(CameraKitEventCallback<CameraKitImage> { event -> imageCaptured(event) })
            }
    }
}
