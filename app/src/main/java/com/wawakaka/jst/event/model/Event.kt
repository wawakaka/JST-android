package com.wawakaka.jst.event.model

import com.wawakaka.jst.base.model.Emptiable
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.pengeluaran.model.Pengeluaran
import java.io.Serializable

data class Event(val id: Int?,
                 val tanggalMulai: String?,
                 val tanggalSelesai: String?,
                 val listKelas: MutableList<Kelas>?,
                 val listPengeluaran: MutableList<Pengeluaran>?,
                 val sekolahNama: String?) : Serializable, Emptiable {

    companion object {
        val empty = Event(null, null, null, null, null, null)
    }

    override fun isEmpty(): Boolean = id != null

    override fun isNotEmpty(): Boolean = !isEmpty()
}