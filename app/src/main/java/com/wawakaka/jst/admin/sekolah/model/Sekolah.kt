package com.wawakaka.jst.admin.sekolah.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 12/30/2017.
 */
data class Sekolah(val nama: String?) : Serializable, Emptiable {

    companion object {
        val empty = Sekolah(null)
    }

    override fun isEmpty(): Boolean {
        return !nama.isNullOrBlank()
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}