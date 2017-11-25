package com.wawakaka.jst.base.model

import android.support.annotation.IntDef

/**
 * Created by wawakaka on 7/25/2017.
 */
class RequestPermissionResultStatus(var requestCode: Int,
                                    var permissions: List<String>?,
                                    @StatusDef var status: Int) {

    companion object {
        const val DENIED = 0
        const val DENIED_NEVER_ASK = 1
        const val GRANTED = 2
    }

    fun isGranted(): Boolean = status == GRANTED

    fun isDeniedNeverAsk(): Boolean? = status == DENIED_NEVER_ASK

    @IntDef(DENIED.toLong(), DENIED_NEVER_ASK.toLong(), GRANTED.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class StatusDef

}