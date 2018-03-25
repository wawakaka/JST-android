package com.wawakaka.jst.pengeluaran.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.view.RxView
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
import com.wawakaka.jst.pengeluaran.model.Pengeluaran
import com.wawakaka.jst.pengeluaran.presenter.PengeluaranPresenter
import com.wawakaka.jst.pengeluaran.view.PengeluaranHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pengeluaran.*

class PengeluaranActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = PengeluaranActivity::class.java.simpleName
    }

    private val pengeluaranPresenter: PengeluaranPresenter by lazy {
        JstApplication.component.providePengeluaranPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initSwipeRefreshPengeluaran()
        initListenToRefreshListEvent()
        initListPengeluaran()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_pengeluaran)
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
                    pengeluaranPresenter.publishRefreshListPengeluaranEvent()
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
                    pengeluaranPresenter.publishRefreshListPengeluaranEvent()
                })
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { pengeluaranPresenter.listenRefreshListPengeluaranEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryLoadPengeluaran() }
    }

    private fun initSwipeRefreshPengeluaran() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                pengeluaran_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(pengeluaran_list_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                pengeluaranPresenter.publishRefreshListPengeluaranEvent()
            }
    }

    private fun retryLoadPengeluaran() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { pengeluaranPresenter.loadUserPengeluaranObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createPengeluaranHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListPengeluaran()
                },
                {
                    LogUtils.error(TAG, "error in retryLoadPengeluaran", it)
                    onLoadLoadPengeluaranError(it)
                }
            )
    }

    private fun initListPengeluaran() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { pengeluaranPresenter.loadUserPengeluaranObservable() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createPengeluaranHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showListPengeluaran()
                },
                {
                    LogUtils.error(TAG, "error in initListPengeluaran", it)
                    onLoadLoadPengeluaranError(it)
                }
            )
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_pengeluaran_container.layoutManager = layoutManager
        list_pengeluaran_container.adapter = adapter
    }

    //todo update result empty error view to display add button rather than error view
    private fun onLoadLoadPengeluaranError(throwable: Throwable) {
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
                //todo add empty screen or leave it blank
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

    private fun createPengeluaranHolderList(pengeluaran: MutableList<Pengeluaran>) {
        list.clear()
        pengeluaran.forEach { list.add(PengeluaranHolder(it)) }
    }

    private fun showListPengeluaran() {
        hideAllViews()
        list_pengeluaran_container.makeVisible()
        add_pengeluaran.makeVisible()
    }

    private fun initAddButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(add_pengeluaran) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchAddPengeluaranActivity()
            }
    }

    private fun launchAddPengeluaranActivity() {
        val intent = Intent(this, AddOrEditPengeluaranActivity::class.java)
        intent.putExtra(ExtraUtils.IS_EDIT,false)
        startActivity(intent)
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
        add_pengeluaran.makeGone()
        list_pengeluaran_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        pengeluaran_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        pengeluaran_list_refresher?.let { it.isRefreshing = false }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is PengeluaranHolder) {
            launchEditPengeluaranActivity(item.model)
            return true
        }
        return false
    }

    private fun launchEditPengeluaranActivity(pengeluaran: Pengeluaran) {
        val intent = Intent(this, AddOrEditPengeluaranActivity::class.java)
        intent.putExtra(ExtraUtils.IS_EDIT, true)
        intent.putExtra(ExtraUtils.PENGELUARAN, pengeluaran)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is PengeluaranHolder) {
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
                            getString(R.string.delete_pengeluaran_title),
                            getString(R.string.delete_pengeluaran_message)
                        )
                }
                .filter { it }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    pengeluaranPresenter
                        .deletePengeluaranObservable(item.model.id ?: 0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(Function {
                            onUpdatePengeluaranFailed(it)
                            Observable.just(false)
                        })
                }
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                    { onUpdatePengeluaranSucceded() },
                    { onUpdatePengeluaranFailed(it) }
                )
        }
    }

    private fun onUpdatePengeluaranSucceded() {
        pengeluaranPresenter.publishRefreshListPengeluaranEvent()
    }

    private fun onUpdatePengeluaranFailed(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> showError(getString(R.string.error_network))
            is NoInternetError -> showError(getString(R.string.error_no_internet))
            else -> showError(getString(R.string.delete_pengeluaran_error_message))
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
