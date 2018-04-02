package com.wawakaka.jst.camera.composer

import android.widget.ArrayAdapter
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.camera.presenter.CameraPresenter
import com.wonderkiln.camerakit.CameraKit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by wawakaka on 10/11/2017.
 */
class CameraActivity : BaseActivity() {

    companion object {
        private val TAG = CameraActivity::class.java.simpleName
    }

    private val cameraPresenter: CameraPresenter by lazy {
        JstApplication.component.provideCameraPresenter()
    }

    private var drawerAdapter: ArrayAdapter<*>? = null

    private var cameraMethod = CameraKit.Constants.METHOD_STANDARD
    private var cropOutput = false

    init {
        initLayout()
        initOnResume()
        initOnPause()
        initListenPictureTakenEvent()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_camera)
                camera!!.setMethod(cameraMethod)
                camera!!.setCropOutput(cropOutput)
            }
    }

    private fun initOnResume() {
        RxNavi
            .observe(naviComponent, Event.RESUME)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                camera!!.start()
            }
    }

    private fun initOnPause() {
        RxNavi
            .observe(naviComponent, Event.PAUSE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                camera!!.stop()
            }
    }

    private fun initListenPictureTakenEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { cameraPresenter.listenPictureTakenEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                finish()
            }
    }

}
