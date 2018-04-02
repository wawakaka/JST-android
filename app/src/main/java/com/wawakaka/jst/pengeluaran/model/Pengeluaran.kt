package com.wawakaka.jst.pengeluaran.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 1/31/2018.
 */
data class Pengeluaran(val id: Int?,
                       val status: Boolean?,
                       val tanggal: String?,
                       val userEmail: String?,
                       val barang: String?,
                       val biaya: Int?,
                       val keterangan: String?,
                       val gambar: String?) : Serializable, Emptiable {

    companion object {
        val empty = Pengeluaran(null, null, null, null, null, null, null, null)
    }

    override fun isEmpty(): Boolean = id != null

    override fun isNotEmpty(): Boolean = !isEmpty()

}