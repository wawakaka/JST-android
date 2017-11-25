package com.wawakaka.jst.base.composer

import android.content.Intent
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.component.support.NaviAppCompatActivity
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.base.model.RequestPermissionResultStatus
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.utils.replaceFragmentSafely
import com.wawakaka.jst.datasource.server.model.InvalidTokenError
import com.wawakaka.jst.navigation.composer.NavigationFragment
import com.wawakaka.jst.navigation.model.DrawerItemClickEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by wawakaka on 7/18/2017.
 */
open class BaseActivity : NaviAppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        const val RC_SETTINGS_SCREEN = 1601
    }

    protected val naviComponent: NaviComponent = this

    private val rxBusId = hashCode()

    private var drawerLayout: DrawerLayout? = null
    private var rationale: String? = null
    private var requestCode = -1
    private var permissions: MutableList<String>? = null
    private var listener: Observer<RequestPermissionResultStatus>? = null

    /**
     * Listen if there is [InvalidTokenError] event
     */
    private fun listenToInvalidTokenErrorObservable(): Observable<InvalidTokenError> {
        return RxBus.registerObservable<InvalidTokenError>()
    }

    /**
     * Initialization of navigation drawer
     *
     * @param drawerIcon View icon of navigation drawer, for toggling
     * @param drawerLayout DrawerLayout used by activity
     * @param drawerViewId Resource id of drawer view
     * @param drawerType Type of drawer used. See constant in NavigationFragment.
     */
    fun initNavigationDrawer(drawerIcon: View,
                             drawerLayout: DrawerLayout,
                             drawerViewId: Int,
                             drawerType: Int) {
        drawerIcon.setOnClickListener {
            toggleDrawer()
        }

        this.drawerLayout = drawerLayout
        setNavigationDrawerFragment(drawerViewId, drawerType)
        listenToDrawerItemClickEvent()
    }

    /**
     * Unlock Navigation Drawer
     */
    fun unlockNavigationDrawer() {
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    /**
     * Disable Navigation Drawer to open from swipe gesture
     */
    fun lockNavigationDrawerModeClose() {
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setNavigationDrawerFragment(drawerViewId: Int,
                                            drawerType: Int) {
        val navigationFragment = NavigationFragment.newInstance(rxBusId, drawerType)
        replaceFragmentSafely(navigationFragment, drawerViewId)
    }

    private fun listenToDrawerItemClickEvent() {
        RxBus
            .registerObservable<DrawerItemClickEvent>(rxBusId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { toggleDrawer() }
    }

    fun toggleDrawer() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }


    /**
     * Permissions checking
     *
     * @param permissions List of permissions to be checked
     *
     * @return True if has been granted, false otherwise
     */
    fun hasPermissions(permissions: List<String>): Boolean {
        return EasyPermissions.hasPermissions(
            this, *permissions.toTypedArray()
        )
    }

    /**
     * Request permissions for Android M and higher
     *
     * @param rationale   String reason why user needs to grant this permissions
     * @param requestCode Used for activity result
     * @param permissions List of permissions requested
     */
    fun requestPermissions(rationale: String,
                           requestCode: Int,
                           permissions: MutableList<String>,
                           listener: Observer<RequestPermissionResultStatus>) {
        this.rationale = rationale
        this.requestCode = requestCode
        this.permissions = permissions
        this.listener = listener

        EasyPermissions.requestPermissions(
            this,
            rationale,
            requestCode,
            *permissions.toTypedArray()
        )
    }

    override fun onPermissionsGranted(requestCode: Int, permissions: MutableList<String>) {
        listener?.onNext(RequestPermissionResultStatus(
            requestCode, permissions, RequestPermissionResultStatus.GRANTED
        ))
    }

    override fun onPermissionsDenied(requestCode: Int, permissions: MutableList<String>) {
        listener?.onNext(RequestPermissionResultStatus(
            requestCode, permissions, RequestPermissionResultStatus.DENIED
        ))

        val deniedPermissionNeverAskAgain = EasyPermissions.somePermissionPermanentlyDenied(this, permissions)

        if (deniedPermissionNeverAskAgain) {
            AppSettingsDialog.Builder(this)
                .setTitle("Permission Denied")
                .setPositiveButton("Grand Permission")
                .setNegativeButton("Never Ask Again")
                .setRequestCode(RC_SETTINGS_SCREEN)
                .build()
                .show()
        }

        if (!deniedPermissionNeverAskAgain) {
            listener?.onNext(RequestPermissionResultStatus(
                this.requestCode, permissions, RequestPermissionResultStatus.DENIED_NEVER_ASK
            ))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SETTINGS_SCREEN) {
            if (permissions?.isEmpty() ?: true) {
                return
            }

            if (EasyPermissions.hasPermissions(this, *permissions?.toTypedArray() ?: arrayOf())) {
                listener?.onNext(RequestPermissionResultStatus(
                    this.requestCode, permissions, RequestPermissionResultStatus.GRANTED
                ))
            } else {
                listener?.onNext(RequestPermissionResultStatus(
                    this.requestCode, permissions, RequestPermissionResultStatus.DENIED_NEVER_ASK
                ))
            }
        }
    }


}