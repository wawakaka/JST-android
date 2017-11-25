package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class KelasRequestResponse(val kelas: MutableList<Kelas>) : Emptiable {

    override fun isEmpty(): Boolean {
        return kelas.isEmpty()
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}