package com.wawakaka.jst.pengeluaran.presenter

import com.wawakaka.jst.base.presenter.BasePresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager

/**
 * Created by babang on 1/28/2018.
 */
class PengeluaranPresenter(private val serverRequestManager: ServerRequestManager,
                           private val localRequestManager: LocalRequestManager) : BasePresenter() {

    companion object {
        private val TAG = PengeluaranPresenter::class.java.simpleName
    }


}