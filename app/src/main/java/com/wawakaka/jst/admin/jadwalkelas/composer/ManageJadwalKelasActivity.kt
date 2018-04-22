package com.wawakaka.jst.admin.jadwalkelas.composer

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
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.IS_EDIT
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.JADWAL_KELAS
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.jadwal.view.JadwalHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage_jadwal_kelas.*

class ManageJadwalKelasActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = ManageJadwalKelasActivity::class.java.simpleName
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
        initSwipeRefreshJadwalKelas()
        initListenToRefreshListEvent()
        initListJadwalKelas()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_manage_jadwal_kelas)
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
                        adminPresenter.publishRefreshListJadwalKelasEvent()
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
                        adminPresenter.publishRefreshListJadwalKelasEvent()
                    })
                }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
                .observe(this, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.listenRefreshListJadwalKelasEvent() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { retryLoadJadwalKelas() }
    }

    private fun initSwipeRefreshJadwalKelas() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    jadwal_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                    RxSwipeRefreshLayout.refreshes(jadwal_list_refresher)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    adminPresenter.publishRefreshListJadwalKelasEvent()
                }
    }

    private fun retryLoadJadwalKelas() {
        Observable
                .just(true)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showLoadProgress() }
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.loadAllJadwalKelasObservable() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createJadwalKelasHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListJadwalKelas()
                        },
                        {
                            LogUtils.error(TAG, "error in retryLoadJadwalKelas", it)
                            onLoadLoadKelasError(it)
                        }
                )
    }

    private fun initListJadwalKelas() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showLoadProgress() }
                .doOnNext { initLayoutManager() }
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.loadAllJadwalKelasObservable() }
                .filter { it.isNotEmpty() }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    createJadwalKelasHolderList(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            adapter.updateDataSet(list)
                            showListJadwalKelas()
                        },
                        {
                            LogUtils.error(TAG, "error in initListJadwalKelas", it)
                            onLoadLoadKelasError(it)
                        }
                )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_schedule_container.layoutManager = layoutManager
        list_schedule_container.adapter = adapter
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
                showListJadwalKelas()
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

    private fun createJadwalKelasHolderList(jadwalKelas: MutableList<JadwalKelas>) {
        list.clear()
        jadwalKelas.forEach { list.add(JadwalHolder(it)) }
    }

    private fun showListJadwalKelas() {
        hideAllViews()
        add_jadwal_kelas.makeVisible()
        list_schedule_container.makeVisible()
    }

    private fun initAddButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(add_jadwal_kelas) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    launchAddOrEditActivity(JadwalKelas.empty, false)
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
        add_jadwal_kelas.makeGone()
        list_schedule_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        jadwal_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        jadwal_list_refresher?.let { it.isRefreshing = false }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is JadwalHolder) {
            launchAddOrEditActivity(item.model, true)
            return true
        }
        return false
    }

    private fun launchAddOrEditActivity(jadwalKelas: JadwalKelas,
                                        isEdit: Boolean) {
        val intent = Intent(this, AddOrEditJadwalKelasActivity::class.java)
        intent.putExtra(IS_EDIT, isEdit)
        intent.putExtra(JADWAL_KELAS, jadwalKelas)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is JadwalHolder) {
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
                                        getString(R.string.delete_jadwal_title),
                                        getString(R.string.delete_jadwal_message)
                                )
                    }
                    .filter { it }
                    .doOnNext { showProgressDialog() }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        adminPresenter
                                .deleteJadwalKelasObservable(item.model)
                                .observeOn(AndroidSchedulers.mainThread())
                                .onErrorResumeNext(Function {
                                    onDeleteJadwalKelasFailed(it)
                                    Observable.just(false)
                                })
                    }
                    .filter { it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                    .subscribe(
                            { onDeleteJadwalKelasSucceded() },
                            { onDeleteJadwalKelasFailed(it) }
                    )
        }
    }

    private fun onDeleteJadwalKelasSucceded() {
        adminPresenter.publishRefreshListJadwalKelasEvent()
        hideProgressDialog()
    }

    private fun onDeleteJadwalKelasFailed(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> showError(getString(R.string.error_network))
            is NoInternetError -> showError(getString(R.string.error_no_internet))
            else -> showError(getString(R.string.delete_jadwal_error_message))
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