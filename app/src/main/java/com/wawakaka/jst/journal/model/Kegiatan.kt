package com.wawakaka.jst.journal.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by babang on 1/27/2018.
 */
data class Kegiatan(val id: Int?,
                    val sesiMulai: String?,
                    val sesiSelesai: String?,
                    val materi: String?,
                    val keterangan: String?,
                    val jadwalKelaId: Int?) : Serializable, Emptiable {

    companion object {
        val empty = Kegiatan(null, null, null, null, null, null)
    }

    override fun isEmpty(): Boolean {
        return id == null
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}