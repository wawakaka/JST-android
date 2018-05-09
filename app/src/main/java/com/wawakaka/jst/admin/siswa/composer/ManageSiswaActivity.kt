package com.wawakaka.jst.admin.siswa.composer

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
import com.wawakaka.jst.admin.view.SiswaHolder
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.IS_EDIT
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.SISWA
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage_siswa.*

class ManageSiswaActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = ManageSiswaActivity::class.java.simpleName
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
        initSwipeRefreshSiswa()
        initListenToRefreshListEvent()
        initListSiswa()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_manage_siswa)
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
                    adminPresenter.publishRefreshListSiswaEvent()
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
                    adminPresenter.publishRefreshListSiswaEvent()
                })
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { adminPresenter.listenRefreshListSiswaEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryLoadSiswa() }
    }

    private fun initSwipeRefreshSiswa() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                siswa_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(siswa_list_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adminPresenter.publishRefreshListSiswaEvent()
            }
    }

    private fun retryLoadSiswa() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { adminPresenter.loadAllSiswaObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createSiswaHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListSiswa()
                },
                {
                    LogUtils.error(TAG, "error in retryLoadSiswa", it)
                    onLoadLoadKelasError(it)
                }
            )
    }

    private fun initListSiswa() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { adminPresenter.loadAllSiswaObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createSiswaHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListSiswa()
                },
                {
                    LogUtils.error(TAG, "error in initListSiswa", it)
                    onLoadLoadKelasError(it)
                }
            )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_siswa_container.layoutManager = layoutManager
        list_siswa_container.adapter = adapter
    }

    private fun initAddButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(add_siswa) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchAddOrEditActivity(Siswa.empty, false)
            }
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

    private fun createSiswaHolderList(jadwalKelas: MutableList<Siswa>) {
        list.clear()
        jadwalKelas.forEach { list.add(SiswaHolder(it)) }
    }

    private fun showListSiswa() {
        hideAllViews()
        add_siswa.makeVisible()
        list_siswa_container.makeVisible()
    }

    private fun showNetworkErrorView() {
        hideAllViews()
        add_siswa.makeGone()
        network_error_view.makeVisible()
        network_error_view.isEnabled = true
    }

    private fun showUnknownErrorView() {
        hideAllViews()
        add_siswa.makeGone()
        unknown_error_view.makeVisible()
        unknown_error_view.isEnabled = true
    }

    private fun showResultEmptyErrorView() {
        hideAllViews()
        result_empty_error_view.makeVisible()
        result_empty_error_view.isEnabled = true
    }

    private fun hideAllViews() {
        list_siswa_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        result_empty_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        siswa_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        siswa_list_refresher?.let { it.isRefreshing = false }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is SiswaHolder) {
            launchAddOrEditActivity(item.model, true)
            return true
        }
        return false
    }

    private fun launchAddOrEditActivity(siswa: Siswa,
                                        isEdit: Boolean) {
        val intent = Intent(this, AddOrEditSiswaActivity::class.java)
        intent.putExtra(IS_EDIT, isEdit)
        intent.putExtra(SISWA, siswa)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is SiswaHolder) {
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
                            getString(R.string.delete_siswa_title),
                            getString(R.string.delete_siswa_message)
                        )
                }
                .filter { it }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    adminPresenter
                        .updateStatusSiswaObservable(item.model)
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(Function {
                            onUpdateSiswaFailed(it)
                            Observable.just(false)
                        })
                }
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                    { onUpdateSiswaSucceded() },
                    { onUpdateSiswaFailed(it) }
                )
        }
    }

    private fun onUpdateSiswaSucceded() {
        hideProgressDialog()
        adminPresenter.publishRefreshListSiswaEvent()
    }

    private fun onUpdateSiswaFailed(throwable: Throwable) {
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
