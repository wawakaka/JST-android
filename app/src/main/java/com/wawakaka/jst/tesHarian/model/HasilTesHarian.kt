package com.wawakaka.jst.tesHarian.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class HasilTesHarian(val id: Int?,
                          var hasil: String?,
                          val tesHarianId: Int?,
                          val siswaId: String?) : Serializable, Emptiable {

    companion object {
        val empty = HasilTesHarian(null, null, null, null)
    }

    override fun isEmpty(): Boolean {
        return id == null
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}