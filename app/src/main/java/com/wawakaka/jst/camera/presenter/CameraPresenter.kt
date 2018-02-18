package com.wawakaka.jst.camera.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager

/**
 * Created by wawakaka on 1/31/2018.
 */
class CameraPresenter(private val serverRequestManager: ServerRequestManager,
                      private val localRequestManager: LocalRequestManager) : BasePresenter() {


}