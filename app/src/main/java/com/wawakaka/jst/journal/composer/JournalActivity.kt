package com.wawakaka.jst.journal.composer

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
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.IS_EDIT
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.KEGIATAN
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.SESI
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.journal.model.Kegiatan
import com.wawakaka.jst.journal.presenter.JournalPresenter
import com.wawakaka.jst.journal.view.JournalHolder
import com.wawakaka.jst.kelas.composer.KelasActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_journal.*

class JournalActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = JournalActivity::class.java.simpleName
    }

    private val journalPresenter: JournalPresenter by lazy {
        JstApplication.component.provideJournalPresenter()
    }

    private val idJadwalKelas: Int? by lazy {
        intent.getSerializableExtra(KelasActivity.EXTRA_ID_JADWAL) as? Int
    }

    private var progressDialog: DefaultProgressDialog? = null
    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initSwipeRefreshJournal()
        initListenToRefreshListEvent()
        initJournalList()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_journal)
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
                    journalPresenter.publishJournalRefreshListEvent()
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
                    journalPresenter.publishJournalRefreshListEvent()
                })
            }
    }

    private fun initSwipeRefreshJournal() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                list_journal_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(list_journal_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                journalPresenter.publishJournalRefreshListEvent()
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { journalPresenter.listenJournalRefreshListEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryLoadJournal() }
    }

    private fun retryLoadJournal() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { journalPresenter.loadJournal(idJadwalKelas ?: 0) }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext { journalHolderList(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showList()
                },
                {
                    LogUtils.error(TAG, "error in retryLoadJournal", it)
                    onLoadLoadJournalError(it)
                }
            )
    }

    private fun initJournalList() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { journalPresenter.loadJournal(idJadwalKelas ?: 0) }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext { journalHolderList(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    showList()
                },
                {
                    LogUtils.error(TAG, "error in initJournalList", it)
                    onLoadLoadJournalError(it)
                }
            )
    }

    private fun onLoadLoadJournalError(throwable: Throwable) {
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

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_journal.layoutManager = layoutManager
        list_journal.setHasFixedSize(true)
        list_journal.adapter = adapter
    }

    private fun journalHolderList(kegiatan: MutableList<Kegiatan>) {
        list.clear()
        kegiatan.forEach { list.add(JournalHolder(it)) }
    }

    private fun showNetworkErrorView() {
        hideAllViews()
        add_journal.makeGone()
        network_error_view.makeVisible()
        network_error_view.isEnabled = true
    }

    private fun showUnknownErrorView() {
        hideAllViews()
        add_journal.makeGone()
        unknown_error_view.makeVisible()
        unknown_error_view.isEnabled = true
    }

    private fun showResultEmptyErrorView() {
        hideAllViews()
        add_journal.makeVisible()
        result_empty_error_view.makeVisible()
        result_empty_error_view.isEnabled = true
    }

    private fun showList() {
        hideAllViews()
        add_journal.makeVisible()
        list_journal.makeVisible()
    }

    private fun hideAllViews() {
        list_journal.makeGone()
        network_error_view.makeGone()
        unknown_error_view.makeGone()
        result_empty_error_view.makeGone()
        hideLoadProgress()
    }

    private fun showLoadProgress() {
        list_journal_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        list_journal_refresher?.let { it.isRefreshing = false }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is JournalHolder) {
            launchEditJournalActivity(item.model, true)
            return true
        }
        return false
    }

    private fun initAddButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(add_journal) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchEditJournalActivity(Kegiatan.empty, false)
            }
    }

    private fun launchEditJournalActivity(kegiatan: Kegiatan,
                                          isEdit: Boolean) {
        val intent = Intent(this, AddOrEditJournalActivity::class.java)
        intent.putExtra(KEGIATAN, kegiatan)
        intent.putExtra(KelasActivity.EXTRA_ID_JADWAL, idJadwalKelas)
        intent.putExtra(SESI, list.size)
        intent.putExtra(IS_EDIT, isEdit)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is JournalHolder) {
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
                            getString(R.string.delete_journal_title),
                            getString(R.string.delete_journal_message)
                        )
                }
                .filter { it }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    journalPresenter
                        .deleteJournal(item.model.id ?: 0)
                }
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                    { onDeleteJournalSucceded() },
                    { onDeleteJournalFailed(it) }
                )
        }
    }

    private fun onDeleteJournalSucceded() {
        journalPresenter.publishJournalRefreshListEvent()
        hideProgressDialog()
    }

    private fun onDeleteJournalFailed(throwable: Throwable) {
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
