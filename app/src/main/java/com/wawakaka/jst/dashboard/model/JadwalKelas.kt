package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/14/2017.
 */
data class JadwalKelas(val id: Int?,
                       val tanggal: String?,
                       val kelaId: Int?,
                       val listSiswa: MutableList<Siswa>?) : Serializable, Emptiable {

    companion object {
        val empty = JadwalKelas(null, null, null, mutableListOf())
    }

    override fun isEmpty(): Boolean {
        return id == null
    }

    override fun isNotEmpty(): Boolean {
        return !isEmpty()
    }
}