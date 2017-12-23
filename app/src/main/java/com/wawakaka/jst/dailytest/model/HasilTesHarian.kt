package com.wawakaka.jst.dailytest.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class HasilTesHarian(val id: Int?,
                          val hasil: String?,
                          val tesHarianId: Int,
                          val siswaId: String?) : Serializable, Emptiable {

    override fun isEmpty(): Boolean {
        return id == null
    }

    override fun isNotEmpty(): Boolean {
        return !isNotEmpty()
    }
}