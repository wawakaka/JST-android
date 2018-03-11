package com.wawakaka.jst.login.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 10/14/2017.
 */
data class User(val email: String?,
                val nama: String?,
                val image: String?,
                val isSuperUser: Boolean?,
                val isActive: Boolean?,
                val token: String?) : Serializable, Emptiable {

    companion object {
        val empty = User(null, null, null, null, null, null)
    }

    override fun isEmpty(): Boolean = email.isNullOrBlank()

    override fun isNotEmpty(): Boolean = !isEmpty()


}