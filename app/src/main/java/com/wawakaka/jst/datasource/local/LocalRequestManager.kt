package com.wawakaka.jst.datasource.local

import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.local.preference.PreferenceApi
import com.wawakaka.jst.journal.model.Kegiatan
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
        private const val KEY_LIST_SISWA = "list-siswa"
        private const val KEY_KELAS = "kelas"
        private const val KEY_PRESENSI_CHECKED_LIST = "presensi-checked-list"
        private const val KEY_TES_HARIAN = "tes-harian"
        private const val KEY_BIDANG = "bidang"
        private const val KEY_SEKOLAH = "sekolah"
        private const val KEY_JOURNAL = "journal"
    }

    fun isLoggedIn(): Boolean {
        return !getUser().isEmpty()
    }

    fun saveListUser(user: List<User>?) {
        if (user != null) {
            preferenceApi.putListObject(KEY_LIST_USER, user)
        } else {
            preferenceApi.removePreference(KEY_LIST_USER)
        }
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

    fun saveListKelas(kelas: List<Kelas>?) {
        if (kelas != null) {
            preferenceApi.putListObject(KEY_KELAS, kelas)
        } else {
            preferenceApi.removePreference(KEY_KELAS)
        }
    }

    fun getListKelas(): List<Kelas> {
        return preferenceApi.getListObject(KEY_KELAS, listOf(), Kelas::class.java)
    }

    fun savePresensiCheckedList(presensi: List<Presensi>?) {
        if (presensi != null) {
            preferenceApi.putListObject(KEY_PRESENSI_CHECKED_LIST, presensi)
        } else {
            preferenceApi.removePreference(KEY_PRESENSI_CHECKED_LIST)
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
            preferenceApi.removePreference(KEY_TES_HARIAN + idJadwalKelas)
        }
    }

    fun getTesHarian(idJadwalKelas: Int?): TesHarian {
        return preferenceApi.getObject(KEY_TES_HARIAN + idJadwalKelas, TesHarian.empty, TesHarian::class.java)
    }

    fun saveListSiswa(listSiswa: List<Siswa>?) {
        if (listSiswa != null) {
            preferenceApi.putListObject(KEY_LIST_SISWA, listSiswa)
        } else {
            preferenceApi.removePreference(KEY_LIST_SISWA)
        }
    }

    fun getListSiswa(): List<Siswa> {
        return preferenceApi.getListObject(KEY_LIST_SISWA, listOf(), Siswa::class.java)
    }

    fun saveListBidang(bidang: List<Bidang>?) {
        if (bidang != null) {
            preferenceApi.putListObject(KEY_BIDANG, bidang)
        } else {
            preferenceApi.removePreference(KEY_BIDANG)
        }
    }

    fun getListBidang(): List<Bidang> {
        return preferenceApi.getListObject(KEY_BIDANG, listOf(), Bidang::class.java)
    }

    fun saveListSekolah(sekolah: List<Sekolah>?) {
        if (sekolah != null) {
            preferenceApi.putListObject(KEY_SEKOLAH, sekolah)
        } else {
            preferenceApi.removePreference(KEY_SEKOLAH)
        }
    }

    fun getListSekolah(): List<Sekolah> {
        return preferenceApi.getListObject(KEY_SEKOLAH, listOf(), Sekolah::class.java)
    }

    fun saveListJournal(kegiatan: List<Kegiatan>?) {
        if (kegiatan != null) {
            preferenceApi.putListObject(KEY_JOURNAL, kegiatan)
        } else {
            preferenceApi.removePreference(KEY_JOURNAL)
        }
    }

    fun getListJournal(): List<Kegiatan> {
        return preferenceApi.getListObject(KEY_JOURNAL, listOf(), Kegiatan::class.java)
    }

}