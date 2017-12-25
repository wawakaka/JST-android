package com.wawakaka.jst.tesHarian.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/27/2017.
 */
data class TesHarian(val id: Int?,
                     val file: String?,
                     val keterangan: String?,
                     val jadwalKelaId: Int?,
                     var hasilTesHarian: MutableList<HasilTesHarian>?) : Serializable, Emptiable {

    companion object {
        val empty = TesHarian(null, null, null, null, null)
    }

    override fun isEmpty(): Boolean {
        return id == null
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}