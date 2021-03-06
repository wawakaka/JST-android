package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class Bidang(val nama: String?) : Serializable, Emptiable {

    override fun isEmpty(): Boolean {
        return nama.isNullOrBlank()
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

}