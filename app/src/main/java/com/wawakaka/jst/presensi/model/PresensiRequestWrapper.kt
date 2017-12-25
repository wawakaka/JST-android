package com.wawakaka.jst.presensi.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 12/24/2017.
 */
class PresensiRequestWrapper(val presensi: MutableList<Presensi>) : Serializable, Emptiable {

    companion object {
        val empty = PresensiRequestWrapper(mutableListOf())
    }

    override fun isEmpty() = presensi.isEmpty()

    override fun isNotEmpty() = !isEmpty()
}