package com.wawakaka.jst.base.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.login.composer.LoginActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by wawakaka on 11/15/2017.
 */
object LogoutUtils {

    private val TAG = LogoutUtils::class.java.simpleName
    private var progressDialog: DefaultProgressDialog? = null

    /**
     * Show logout confirmation dialog to the user. User will be logged out after pressed ok.
     */
    fun showLogoutConfirmation(activity: Activity,
                               naviComponent: NaviComponent,
                               onLogoutConfirmedObservable: () -> Observable<Boolean>) {
        getLogoutConfirmationObservable(activity)
            .filter { it }
            .doOnNext {
                if (!activity.isFinishing) {
                    showProgressDialog(activity)
                }
            }
            .observeOn(Schedulers.io())
            .flatMap { onLogoutConfirmedObservable() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success logout: $it")
                    onLogoutSucceed(activity)
                },
                {
                    LogUtils.error(TAG, "Error logout", it)
                    onLogoutSucceed(activity)
                },
                { LogUtils.debug(TAG, "Complete logout") }
            )
    }

    private fun getLogoutConfirmationObservable(context: Context): Observable<Boolean> {
        return ViewUtils.showConfirmationObservable(
            context,
                context.getString(R.string.logout_dialog_title_message),
                context.getString(R.string.logout_dialog_message)
        )
    }

    /**
     * Show force logout information dialog to the user. User will be logged out after pressed ok.
     */
    fun showForceLogOutInformation(context: Context,
                                   naviComponent: NaviComponent,
                                   onLogoutConfirmedObservable: () -> Observable<Boolean>) {
        showForceLogoutDialogObservable(context)
                .doOnNext { showProgressDialog(context) }
                .observeOn(Schedulers.io())
                .flatMap { onLogoutConfirmedObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            LogUtils.debug(TAG, "Success logout: $it")
                            onLogoutSucceed(context)
                        },
                        {
                            LogUtils.error(TAG, "Error logout", it)
                            onLogoutSucceed(context)
                        },
                        { LogUtils.debug(TAG, "Complete logout") }
                )
    }

    private fun showForceLogoutDialogObservable(context: Context): Observable<Boolean> {
        return ViewUtils
                .showInfoDialogObservable(
                        context,
                        context.getString(R.string.error_token_expired)
                )
    }

    private fun onLogoutSucceed(context: Context) {
        hideProgressDialog()
        clearAllShownNotifications(context)
        startLoginActivity(context)
    }


    private fun showProgressDialog(context: Context) {
        getProgressDialog(context)?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun getProgressDialog(context: Context): DefaultProgressDialog? {
        progressDialog = DefaultProgressDialog(context, context.getString(R.string.logout_process), false)

        return progressDialog
    }

    private fun clearAllShownNotifications(context: Context) {
        context.clearAllShownNotifications()
    }

    private fun startLoginActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntent(intent)
        taskStackBuilder.startActivities()
    }

}