package com.wawakaka.jst.admin.bidang.model

import com.wawakaka.jst.base.model.Emptiable
import java.io.Serializable

data class BidangUser(val id: Int?,
                      val user_email: Boolean = true,
                      val kela_id: Int?) : Serializable, Emptiable {

    companion object {
        val empty = BidangUser(null, false, null)
    }

    override fun isEmpty(): Boolean = id == null

    override fun isNotEmpty(): Boolean = !isEmpty()

}