package com.wawakaka.jst.admin.composer

import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.view.KelasHolder
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage_kelas.*

class ManageKelasActivity : BaseActivity(), FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = ManageKelasActivity::class.java.simpleName
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initSwipeRefreshKelas()
        initListenToRefreshListEvent()
        initListKelas()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_manage_kelas)
                    initToolbar()
                }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNetworkErrorView() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    network_error_view.setActionOnClickListener(View.OnClickListener {
                        network_error_view.isEnabled = false
                        adminPresenter.publishRefreshListKelasEvent()
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
                        adminPresenter.publishRefreshListKelasEvent()
                    })
                }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
                .observe(this, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.listenRefreshListKelasEvent() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { retryLoadKelas() }
    }

    private fun initSwipeRefreshKelas() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    kelas_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                    RxSwipeRefreshLayout.refreshes(kelas_list_refresher)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    adminPresenter.publishRefreshListKelasEvent()
                }
    }

    private fun retryLoadKelas() {
        Observable
                .just(true)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showLoadProgress() }
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.loadAllKelasObservable() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createKelasHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListKelas()
                        },
                        {
                            LogUtils.error(TAG, "error in retryLoadKelas", it)
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
                .flatMap { adminPresenter.loadAllKelasObservable() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createKelasHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListKelas()
                        },
                        {
                            LogUtils.error(TAG, "error in initListKelas", it)
                            onLoadLoadKelasError(it)
                        }
                )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_kelas_container.layoutManager = layoutManager
        list_kelas_container.adapter = adapter
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

    private fun createKelasHolderList(jadwalKelas: MutableList<Kelas>) {
        list.clear()
        jadwalKelas.forEach { list.add(KelasHolder(it)) }
    }

    private fun showListKelas() {
        hideAllViews()
        list_kelas_container.makeVisible()
    }

    private fun initAddButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(add_kelas) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                }
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
        list_kelas_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        kelas_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        kelas_list_refresher?.let { it.isRefreshing = false }
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is KelasHolder) {
            ViewUtils
                    .showOptionsObservable(
                            this,
                            null,
                            listOf(getString(R.string.update))
                    )
                    .filter { it.isNotBlank() }
                    .flatMap {
                        ViewUtils
                                .showConfirmationObservable(
                                        this,
                                        getString(R.string.delete_kelas_title),
                                        getString(R.string.delete_kelas_message)
                                )
                    }
                    .filter { it }
                    .doOnNext { showProgressDialog() }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        adminPresenter
                                .updateStatusKelasObservable(item.model.id ?: 0)
                                .observeOn(AndroidSchedulers.mainThread())
                                .onErrorResumeNext(Function {
                                    onUpdateKelasFailed(it)
                                    Observable.just(false)
                                })
                    }
                    .filter { it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                    .subscribe(
                            { onUpdateKelasSucceded() },
                            { onUpdateKelasFailed(it) }
                    )
        }
    }

    private fun onUpdateKelasSucceded() {
        adminPresenter.publishRefreshListKelasEvent()
    }

    private fun onUpdateKelasFailed(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> showError(getString(R.string.error_network))
            is NoInternetError -> showError(getString(R.string.error_no_internet))
            else -> showError(getString(R.string.delete_kelas_error_message))
        }
    }

    private fun showError(errorMessage: String) {
        ViewUtils
                .showInfoDialogObservable(this, errorMessage)
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe()
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DefaultProgressDialog(this, getString(R.string.processing), false)
        }
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return if (id == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}