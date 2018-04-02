package com.wawakaka.jst.camera.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.camera.model.PictureTakenEvent

/**
 * Created by wawakaka on 1/31/2018.
 */
class CameraPresenter : BasePresenter() {

    fun publishPictureTakenEvent() {
        RxBus.post(PictureTakenEvent())
    }

    fun listenPictureTakenEvent() = RxBus.registerObservable<PictureTakenEvent>()
}