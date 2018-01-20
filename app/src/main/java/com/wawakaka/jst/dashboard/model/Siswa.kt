package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import java.io.Serializable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class Siswa(val id: String?,
                 val nama: String?,
                 val kela: String?,
                 val isActive: Boolean?,
                 val kelaId: String?,
                 val sekolahNama: String?,
                 val hasilTesHarian: MutableList<HasilTesHarian>?,
                 val laporanAkhir: LaporanAkhir?) : Serializable, Emptiable {

    companion object {
        val empty = Siswa(null, null, null, null, null, null, mutableListOf(), null)
    }

    override fun isEmpty(): Boolean {
        return id.isNullOrBlank()
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

}