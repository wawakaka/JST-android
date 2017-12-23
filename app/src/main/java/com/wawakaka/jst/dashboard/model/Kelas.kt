package com.wawakaka.jst.dashboard.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 11/13/2017.
 */
data class Kelas(val id: Int?,
                 val isPrivate: Boolean = false,
                 val isActive: Boolean = true,
                 val bidangNama: String?,
                 val sekolah: String?,
                 val jadwalKelas: MutableList<JadwalKelas>?,
                 val listSiswa: MutableList<Siswa>?) : Serializable, Emptiable {

    companion object {
        val empty = Kelas(null, false, false, null, null, mutableListOf(), mutableListOf())
    }

    override fun isEmpty(): Boolean = id == null

    override fun isNotEmpty(): Boolean = !isEmpty()

}