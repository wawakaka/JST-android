package com.wawakaka.jst.datasource.local

import com.wawakaka.jst.admin.model.Sekolah
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.preference.PreferenceApi
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.tesHarian.model.TesHarian

/**
 * Created by wawakaka on 11/6/2017.
 */
class LocalRequestManager(private val preferenceApi: PreferenceApi) {

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_LIST_USER = "list-user"
        private const val KEY_KELAS = "kelas"
        private const val KEY_PRESENSI_CHECKED_LIST = "presensi-checked-list"
        private const val KEY_TES_HARIAN = "tes-harian"
        private const val KEY_SISWA = "siswa"
        private const val KEY_BIDANG = "bidang"
        private const val KEY_SEKOLAH = "sekolah"

    }

    fun isLoggedIn(): Boolean {
        return !getUser().isEmpty()
    }

    //todo load list user in admin activity if user is admin
    fun saveListUser(user: List<User>) {
        return preferenceApi.putListObject(KEY_LIST_USER, user)
    }

    fun getListUser(): List<User> {
        return preferenceApi.getListObject(KEY_LIST_USER, listOf(), User::class.java)
    }

    fun saveUser(user: User?) {
        if (user != null) {
            preferenceApi.putObject(KEY_USER, user)
        } else {
            preferenceApi.removePreference(KEY_USER)
        }
    }

    fun getUser(): User {
        return preferenceApi.getObject(KEY_USER, User.empty, User::class.java)
    }

    fun saveListKelas(kelas: List<Kelas>) {
        return preferenceApi.putListObject(KEY_KELAS, kelas)
    }

    fun getListKelas(): List<Kelas> {
        return preferenceApi.getListObject(KEY_KELAS, listOf(), Kelas::class.java)
    }

    fun savePresensiCheckedList(presensi: List<Presensi>) {
        return preferenceApi.putListObject(KEY_PRESENSI_CHECKED_LIST, presensi)
    }

    fun getPresensiCheckedList(): List<Presensi> {
        return preferenceApi.getListObject(KEY_PRESENSI_CHECKED_LIST, listOf(), Presensi::class.java)
    }

    fun saveTesHarian(tesHarian: TesHarian?,
                      idJadwalKelas: Int?) {
        if (tesHarian != null) {
            preferenceApi.putObject(KEY_TES_HARIAN + idJadwalKelas, tesHarian)
        } else {
            preferenceApi.removePreference(KEY_TES_HARIAN)
        }
    }

    fun getTesHarian(idJadwalKelas: Int?): TesHarian {
        return preferenceApi.getObject(KEY_TES_HARIAN + idJadwalKelas, TesHarian.empty, TesHarian::class.java)
    }

    fun saveListSiswa(listSiswa: List<Siswa>) {
        return preferenceApi.putListObject(KEY_SISWA, listSiswa)
    }

    fun getListSiswa(): List<Siswa> {
        return preferenceApi.getListObject(KEY_SISWA, listOf(), Siswa::class.java)
    }

    fun saveListBidang(bidang: List<Bidang>) {
        return preferenceApi.putListObject(KEY_BIDANG, bidang)
    }

    fun getListBidang(): List<Bidang> {
        return preferenceApi.getListObject(KEY_BIDANG, listOf(), Bidang::class.java)
    }

    fun saveListSekolah(sekolah: List<Sekolah>) {
        return preferenceApi.putListObject(KEY_SEKOLAH, sekolah)
    }

    fun getListSekolah(): List<Sekolah> {
        return preferenceApi.getListObject(KEY_SEKOLAH, listOf(), Sekolah::class.java)
    }

}