package com.wawakaka.jst.navigation.composer

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseFragment
import com.wawakaka.jst.base.utils.LogoutUtils
import com.wawakaka.jst.base.utils.RxBus
import com.wawakaka.jst.navigation.model.DrawerItemClickEvent
import com.wawakaka.jst.navigation.presenter.NavigationPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_drawer.*

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


        const val DRAWER_TYPE_LOGOUT = 0

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
    private val navigationPresenter: NavigationPresenter by lazy {
        JstApplication.component.provideNavigationPresenter()
    }

    init {
        initLogoutButton()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.inflate(R.layout.fragment_drawer, container, false)
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