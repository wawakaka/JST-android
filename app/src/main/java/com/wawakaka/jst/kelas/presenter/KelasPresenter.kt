package com.wawakaka.jst.kelas.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager

/**
 * Created by wawakaka on 11/25/2017.
 */
class KelasPresenter(private val serverRequestManager: ServerRequestManager,
                     private val localRequestManager: LocalRequestManager) : BasePresenter() {

}