package com.wawakaka.jst.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import com.trello.navi2.Event
import com.trello.navi2.Listener
import com.trello.navi2.NaviComponent
import com.trello.navi2.internal.NaviEmitter

/**
 * Created by wawakaka on 11/13/2017.
 */
abstract class NaviDialog(context: Context) : Dialog(context), NaviComponent {

    private val base = NaviEmitter.createFragmentEmitter()

    override fun handlesEvents(vararg events: Event<*>): Boolean {
        return base.handlesEvents(*events)
    }

    override fun <T> addListener(event: Event<T>, listener: Listener<T>) {
        base.addListener(event, listener)
    }

    override fun <T> removeListener(listener: Listener<T>) {
        base.removeListener(listener)
    }

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        base.onCreate(savedInstanceState)
    }

    @CallSuper override fun onStart() {
        super.onStart()
        base.onStart()
    }

    @CallSuper override fun onStop() {
        base.onStop()
        super.onStop()
    }

}