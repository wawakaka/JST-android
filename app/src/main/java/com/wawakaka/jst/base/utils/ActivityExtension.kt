package com.wawakaka.jst.base.utils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by wawakaka on 11/15/2017.
 */

/**
 * Method to replace the fragment. The [fragment] is added to the container view with id
 * [containerViewId]. The operation is performed by the supportFragmentManager.
 */
fun AppCompatActivity.replaceFragmentSafely(fragment: Fragment,
                                            @IdRes containerViewId: Int) {
    val fragmentTransaction = supportFragmentManager
        .beginTransaction()
        .replace(containerViewId, fragment)
    if (!supportFragmentManager.isStateSaved) {
        fragmentTransaction.commit()
    } else {
        fragmentTransaction.commitAllowingStateLoss()
    }
}