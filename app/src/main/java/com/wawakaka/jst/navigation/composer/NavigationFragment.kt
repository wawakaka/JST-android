package com.wawakaka.jst.navigation.composer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.composer.AdminActivity
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseFragment
import com.wawakaka.jst.base.utils.LogoutUtils
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.composer.DashboardActivity
import com.wawakaka.jst.navigation.model.DrawerItemClickEvent
import com.wawakaka.jst.navigation.presenter.NavigationPresenter
import com.wawakaka.jst.pengeluaran.composer.PengeluaranActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_drawer.*
import java.util.concurrent.TimeUnit

/**
 * Created by wawakaka on 11/15/2017.
 */
class NavigationFragment : BaseFragment() {

    companion object {
        private val TAG = NavigationFragment::class.java.simpleName
        private val ACTION_DRAWER_TYPE = "action_drawer_type"
        private val RX_BUS_ID = "rx_bus_id"

        /**
         * Approximate delay time used to wait the navigation drawer until it is closed. Used to
         * avoid lag.
         */
        private const val DELAY_TIME = 250L


        const val DRAWER_TYPE_KELAS = 0
        const val DRAWER_TYPE_ADMIN = 1
        const val DRAWER_TYPE_PENGELUARAN = 2
        const val DRAWER_TYPE_LOGOUT = 3

        fun newInstance(rxBusId: Int, activeDrawerType: Int): NavigationFragment {
            val bundle = Bundle()
            bundle.putInt(RX_BUS_ID, rxBusId)
            bundle.putInt(ACTION_DRAWER_TYPE, activeDrawerType)

            val drawerFragment = NavigationFragment()
            drawerFragment.arguments = bundle

            return drawerFragment
        }
    }

    private val naviComponent: NaviComponent = this
    private val rxBusId: Int by lazy { arguments!!.getInt(RX_BUS_ID, -1) }
    private val activeDrawerType: Int by lazy { arguments!!.getInt(ACTION_DRAWER_TYPE, DRAWER_TYPE_KELAS) }
    private val navigationPresenter: NavigationPresenter by lazy {
        JstApplication.component.provideNavigationPresenter()
    }

    init {
        initSelectedButton()
        initKelasButton()
        initPengeluaranButton()
        initAdminButton()
        initLogoutButton()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }

    private fun initSelectedButton() {
        RxNavi
                .observe(naviComponent, Event.VIEW_CREATED)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY_VIEW))
                .subscribe {
                    when (activeDrawerType) {
                        DRAWER_TYPE_KELAS -> drawer_item_kelas.isSelected = true
                        DRAWER_TYPE_ADMIN -> drawer_item_admin.isSelected = true
                    }
                }
    }

    private fun initKelasButton() {
        RxNavi
                .observe(naviComponent, Event.VIEW_CREATED)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(drawer_item_kelas) }
                .doOnNext { RxBus.post(rxBusId, DrawerItemClickEvent(DRAWER_TYPE_KELAS)) }
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS)
                .filter { activeDrawerType != DRAWER_TYPE_KELAS }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY_VIEW))
                .subscribe {
                    launchDashboardActivity()
                }
    }

    private fun launchDashboardActivity() {
        val intent = Intent(activity, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun initAdminButton() {
        RxNavi
                .observe(naviComponent, Event.VIEW_CREATED)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showThisMenuIfUserIsAdmin() }
                .flatMap { RxView.clicks(drawer_item_admin) }
                .doOnNext { RxBus.post(rxBusId, DrawerItemClickEvent(DRAWER_TYPE_ADMIN)) }
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS)
                .filter { activeDrawerType != DRAWER_TYPE_ADMIN }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY_VIEW))
                .subscribe {
                    launchAdminActivity()
                }
    }

    private fun showThisMenuIfUserIsAdmin() {
        if (navigationPresenter.getUser().isSuperUser == true) {
            drawer_item_admin.makeVisible()
        } else {
            drawer_item_admin.makeGone()
        }
    }

    private fun launchAdminActivity() {
        val intent = Intent(activity, AdminActivity::class.java)
        startActivity(intent)
    }

    private fun initPengeluaranButton() {
        RxNavi
            .observe(naviComponent, Event.VIEW_CREATED)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(drawer_item_pengeluaran) }
            .doOnNext { RxBus.post(rxBusId, DrawerItemClickEvent(DRAWER_TYPE_KELAS)) }
            .delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .filter { activeDrawerType != DRAWER_TYPE_PENGELUARAN }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY_VIEW))
            .subscribe {
                launchPengeluaranActivity()
            }
    }

    private fun launchPengeluaranActivity() {
        val intent = Intent(activity, PengeluaranActivity::class.java)
        startActivity(intent)
    }

    private fun initLogoutButton() {
        RxNavi
            .observe(naviComponent, Event.VIEW_CREATED)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(drawer_item_logout) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY_VIEW))
            .subscribe {
                RxBus.post(rxBusId, DrawerItemClickEvent(DRAWER_TYPE_LOGOUT))
                LogoutUtils.showLogoutConfirmation(
                    activity as Activity,
                    naviComponent,
                    { navigationPresenter.onLogoutConfirmedObservable() }
                )
            }
    }

}