package com.wawakaka.jst.event.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.event.presenter.EventPresenter
import com.wawakaka.jst.event.view.EventHolder
import com.wawakaka.jst.navigation.composer.NavigationFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_event.*

class EventActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {

    companion object {
        private val TAG = EventActivity::class.java.simpleName
    }

    private val eventPresenter: EventPresenter by lazy {
        JstApplication.component.provideEventPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNavigationDrawer()
        initNetworkErrorView()
        initUnknownErrorView()
        initListenToRefreshListEvent()
        initSwipeRefreshEventList()
        initListEvent()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_event)
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
                    NavigationFragment.DRAWER_TYPE_EVENT
                )
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
                    eventPresenter.publishRefreshListEvent()
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
                    eventPresenter.publishRefreshListEvent()
                })
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { eventPresenter.listenRefreshListEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryLoadEventList() }
    }

    private fun initSwipeRefreshEventList() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                event_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(event_list_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                eventPresenter.publishRefreshListEvent()
            }
    }

    private fun retryLoadEventList() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { eventPresenter.loadAllEventObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createEventHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListEvent()
                },
                {
                    LogUtils.error(TAG, "error in retryLoadEventList", it)
                    onLoadLoadEventError(it)
                }
            )
    }

    private fun initListEvent() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { eventPresenter.loadAllEventObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createEventHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListEvent()
                },
                {
                    LogUtils.error(TAG, "error in initListEvent", it)
                    onLoadLoadEventError(it)
                }
            )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_event_container.layoutManager = layoutManager
        list_event_container.adapter = adapter
    }

    private fun onLoadLoadEventError(throwable: Throwable) {
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
            is ResultEmptyError -> {
                adapter.updateDataSet(mutableListOf())
                showResultEmptyErrorView()
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

    private fun createEventHolderList(event: MutableList<com.wawakaka.jst.event.model.Event>) {
        list.clear()
        event.forEach { list.add(EventHolder(this, it)) }
    }

    private fun showListEvent() {
        hideAllViews()
        list_event_container.makeVisible()
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

    private fun showResultEmptyErrorView() {
        hideAllViews()
        result_empty_error_view.makeVisible()
        result_empty_error_view.isEnabled = true
    }

    private fun hideAllViews() {
        list_event_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        result_empty_error_view.makeGone()
        hideLoadProgress()
        hideProgressDialog()
    }

    private fun showLoadProgress() {
        event_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        event_list_refresher?.let { it.isRefreshing = false }
        hideProgressDialog()
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is EventHolder) {
            launchEventMenuActivity(item.model)
            return true
        }
        return false
    }

    private fun launchEventMenuActivity(event: com.wawakaka.jst.event.model.Event) {
        LogUtils.debug(TAG, event.toString())
        val intent = Intent(this, EventMenuActivity::class.java)
        intent.putExtra(ExtraUtils.EVENT, event)
        startActivity(intent)
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

}
