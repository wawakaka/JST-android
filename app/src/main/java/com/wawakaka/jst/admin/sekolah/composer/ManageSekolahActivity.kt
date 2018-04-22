package com.wawakaka.jst.admin.sekolah.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.admin.view.SekolahHolder
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage_sekolah.*

class ManageSekolahActivity : BaseActivity(), FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = ManageSekolahActivity::class.java.simpleName
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
        initSwipeRefreshSekolah()
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
                    setContentView(R.layout.activity_manage_sekolah)
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
                        adminPresenter.publishRefreshListSekolahEvent()
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
                        adminPresenter.publishRefreshListSekolahEvent()
                    })
                }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
                .observe(this, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.listenRefreshListSekolahEvent() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { retryLoadSekolah() }
    }

    private fun initSwipeRefreshSekolah() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    sekolah_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                    RxSwipeRefreshLayout.refreshes(sekolah_list_refresher)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    adminPresenter.publishRefreshListSekolahEvent()
                }
    }

    private fun retryLoadSekolah() {
        Observable
                .just(true)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showLoadProgress() }
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.loadAllSekolah() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createSekolahHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListSekolah()
                        },
                        {
                            LogUtils.error(TAG, "error in retryLoadSekolah", it)
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
                .flatMap { adminPresenter.loadAllSekolah() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createSekolahHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListSekolah()
                        },
                        {
                            LogUtils.error(TAG, "error in initListKelas", it)
                            onLoadLoadKelasError(it)
                        }
                )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_sekolah_container.layoutManager = layoutManager
        list_sekolah_container.adapter = adapter
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
            is ResultEmptyError -> {
                adapter.updateDataSet(mutableListOf())
                showListSekolah()
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

    private fun createSekolahHolderList(sekolah: MutableList<Sekolah>) {
        list.clear()
        sekolah.forEach { list.add(SekolahHolder(it)) }
    }

    private fun showListSekolah() {
        hideAllViews()
        add_sekolah.makeVisible()
        list_sekolah_container.makeVisible()
    }

    private fun initAddButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(add_sekolah) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    launchAddActivity()
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
        add_sekolah.makeGone()
        list_sekolah_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        sekolah_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        sekolah_list_refresher?.let { it.isRefreshing = false }
    }

    private fun launchAddActivity() {
        val intent = Intent(this, AddOrEditSekolahActivity::class.java)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is SekolahHolder) {
            ViewUtils
                    .showOptionsObservable(
                            this,
                            null,
                            listOf(getString(R.string.delete))
                    )
                    .filter { it.isNotBlank() }
                    .flatMap {
                        ViewUtils
                                .showConfirmationObservable(
                                        this,
                                        getString(R.string.delete_sekolah_title),
                                        getString(R.string.delete_sekolah_message)
                                )
                    }
                    .filter { it }
                    .doOnNext { showProgressDialog() }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        adminPresenter
                                .deleteSekolahObservable(item.model.nama ?: "")
                                .observeOn(AndroidSchedulers.mainThread())
                                .onErrorResumeNext(Function {
                                    onDeleteSekolahFailed(it)
                                    Observable.just(false)
                                })
                    }
                    .filter { it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                    .subscribe(
                            { onDeleteSekolahSucceded() },
                            { onDeleteSekolahFailed(it) }
                    )
        }
    }

    private fun onDeleteSekolahSucceded() {
        hideProgressDialog()
        adminPresenter.publishRefreshListSekolahEvent()
    }

    private fun onDeleteSekolahFailed(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> showError(getString(R.string.error_network))
            is NoInternetError -> showError(getString(R.string.error_no_internet))
            else -> showError(getString(R.string.delete_sekolah_error_message))
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