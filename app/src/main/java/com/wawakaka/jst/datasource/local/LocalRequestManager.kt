package com.wawakaka.jst.datasource.local

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
        private const val KEY_KELAS = "kelas"
        private const val KEY_PRESENSI_CHECKED_LIST = "presensi-checked-list"
        private const val KEY_TES_HARIAN = "tes-harian"
        private const val KEY_SISWA = "siswa"

    }

    fun isLoggedIn(): Boolean {
        return !getUser().isEmpty()
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

    fun saveKelas(kelas: List<Kelas>) {
        if (kelas != null) {
            return preferenceApi.putListObject(KEY_KELAS, kelas)
        } else {
            preferenceApi.removePreference(KEY_KELAS)
        }
    }

    fun getListKelas(): List<Kelas> {
        return preferenceApi.getListObject(KEY_KELAS, listOf(), Kelas::class.java)
    }

    fun savePresensiCheckedList(presensi: List<Presensi>) {
        if (presensi != null) {
            return preferenceApi.putListObject(KEY_PRESENSI_CHECKED_LIST, presensi)
        } else {
            preferenceApi.removePreference(KEY_KELAS)
        }
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

}