package com.wawakaka.jst.login.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

data class UserLoginRequest(val user: User?) : Serializable, Emptiable {

    companion object {
        val empty = UserLoginRequest(null)
    }

    override fun isEmpty(): Boolean = user?.isEmpty() ?: false

    override fun isNotEmpty(): Boolean = !isEmpty()

}