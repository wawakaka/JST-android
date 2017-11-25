package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/15/2017.
 */
data class Siswa(val id: String?,
                 val nama: String?,
                 val kelas: String?,
                 val isActive: String?,
                 val hasilTesHarian: HasilTesHarian?,
                 val laporanAkhir: LaporanAkhir?) : Serializable, Emptiable {

    override fun isEmpty(): Boolean {
        return id.isNullOrBlank()
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

}