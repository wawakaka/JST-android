package com.wawakaka.jst.datasource.local

import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.datasource.local.preference.PreferenceApi
import com.wawakaka.jst.login.model.User

/**
 * Created by wawakaka on 11/6/2017.
 */
class LocalRequestManager(private val preferenceApi: PreferenceApi) {

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_KELAS = "kelas"
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

}