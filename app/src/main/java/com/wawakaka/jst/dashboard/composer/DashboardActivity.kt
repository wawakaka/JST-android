package com.wawakaka.jst.dashboard.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import com.wawakaka.jst.dashboard.view.ClassHolder
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.navigation.composer.NavigationFragment
import com.wawakaka.jst.schedule.composer.ScheduleActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {

    companion object {
        private val TAG = DashboardActivity::class.java.simpleName
    }

    private val dashboardPresenter: DashboardPresenter by lazy {
        JstApplication.component.provideDashboardPresenter()
    }

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initNavigationDrawer()
        initListenToRefreshListEvent()
        initSwipeRefreshKelas()
        initListKelas()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_dashboard)
            }
    }

    private fun initNetworkErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                network_error_view.setActionOnClickListener(View.OnClickListener {
                    network_error_view.isEnabled = false
                    dashboardPresenter.publishRefreshListKelasEvent()
                })
            }
    }

    private fun initUnknownErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                unknown_error_view.setActionOnClickListener(View.OnClickListener {
                    unknown_error_view.isEnabled = false
                    dashboardPresenter.publishRefreshListKelasEvent()
                })
            }
    }

    private fun initNavigationDrawer() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                initNavigationDrawer(
                    navigation_drawer_icon,
                    navigation_drawer,
                    R.id.dashboard_drawer_fragment,
                    NavigationFragment.DRAWER_TYPE_LOGOUT
                )
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { dashboardPresenter.listenRefreshListKelasEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryLoadKelas() }
    }

    private fun initSwipeRefreshKelas() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Kelas_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(Kelas_list_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                dashboardPresenter.publishRefreshListKelasEvent()
            }
    }

    private fun retryLoadKelas() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { dashboardPresenter.loadClassObservable() }
            .filter { it != null && it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createClassHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showKelasView()
                },
                {
                    LogUtils.error(TAG, "error in initClassList", it)
                    onLoadLoadKelasError(it)
                }
            )
    }

    private fun initListKelas() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { dashboardPresenter.loadClassObservable() }
            .filter { it != null && it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createClassHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showKelasView()
                },
                {
                    LogUtils.error(TAG, "error in initClassList", it)
                    onLoadLoadKelasError(it)
                }
            )
    }

    private fun onLoadLoadKelasError(throwable: Throwable) {
        hideLoadProgress()

        when (throwable) {
            is NetworkError -> {
                if (list.isEmpty()) {
                    showNetworkErrorView()
                } else {
                    showSnackbarError(getString(R.string.error_network))
                }
            }
            is NoInternetError -> {
                if (list.isEmpty()) {
                    showNetworkErrorView()
                } else {
                    showSnackbarError(getString(R.string.error_no_internet))
                }
            }
            else -> {
                if (list.isEmpty()) {
                    showUnknownErrorView()
                } else {
                    showSnackbarError(getString(R.string.error_unknown))
                }
            }
        }
    }

    private fun showSnackbarError(errorString: String) {
        ViewUtils.showSnackbarError(findViewById(android.R.id.content), errorString)
    }

    private fun showKelasView() {
        hideAllViews()
        class_list_container.makeVisible()
    }

    private fun showLoadProgress() {
        Kelas_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        Kelas_list_refresher?.let { it.isRefreshing = false }
    }

    private fun showNetworkErrorView() {
        hideAllViews()
        network_error_view.makeVisible()
        network_error_view.isEnabled = true
    }

    private fun showUnknownErrorView() {
        hideAllViews()
        unknown_error_view.makeVisible()
        unknown_error_view.isEnabled = true
    }

    private fun hideAllViews() {
        class_list_container.makeGone()
        network_error_view.makeGone()
        unknown_error_view.makeGone()
        hideLoadProgress()
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        class_list_container.layoutManager = layoutManager
        class_list_container.adapter = adapter
    }

    private fun createClassHolderList(kelas: MutableList<Kelas>) {
        list.clear()
        kelas.forEach { list.add(ClassHolder(it)) }
    }

    override fun onItemClick(position: Int): Boolean {
        LogUtils.debug(TAG, position.toString())
        val item = adapter.getItem(position)
        if (item is ClassHolder) {
            launchScheduleActivity(item.model)
            LogUtils.debug(TAG, item.model.bidangNama ?: "")
            return true
        }
        return false
    }

    private fun launchScheduleActivity(kelas: Kelas) {
        val intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra(ScheduleActivity.SCHEDULE_INFO, kelas)
        startActivity(intent)
    }

}