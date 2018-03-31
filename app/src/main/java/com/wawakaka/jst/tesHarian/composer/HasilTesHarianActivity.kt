package com.wawakaka.jst.tesHarian.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT_NAME
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import com.wawakaka.jst.tesHarian.model.TesHarian
import com.wawakaka.jst.tesHarian.presenter.TesHarianPresenter
import com.wawakaka.jst.tesHarian.view.HasilTesHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_hasil_tes_harian.*
import java.util.*

class HasilTesHarianActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {

    companion object {
        private val TAG = HasilTesHarianActivity::class.java.simpleName
        private const val EDIT_REQUEST = 2081
        private const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    private val tesHarianPresenter: TesHarianPresenter by lazy {
        JstApplication.component.provideTesHarianPresenter()
    }

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)
    private var tempList: MutableList<HasilTesHarian> = mutableListOf()

    private val idJadwalKelas: Int by lazy {
        intent.getSerializableExtra(EXTRA_ID_JADWAL) as Int
    }

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initResultEmptyErrorView()
        initListenToRefreshListEvent()
        initSwipeRefreshKelas()
        initTesHarian()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_hasil_tes_harian)
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
                })
            }
    }

    private fun initResultEmptyErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                result_empty_error_view.setActionOnClickListener(View.OnClickListener {
                    result_empty_error_view.isEnabled = false
                })
            }
    }

    private fun initListenToRefreshListEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { tesHarianPresenter.listenTesHarianRefreshListEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { retryTesHarian() }
    }

    private fun retryTesHarian() {
        Observable
            .just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadProgress() }
            .observeOn(Schedulers.io())
            .flatMap { tesHarianPresenter.reloadTesHarian(idJadwalKelas) }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext { hasilTesHolder(it.hasilTesHarian ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    adapter.updateDataSet(list)
                    onInitHasilTesSucceed()
                },
                {
                    LogUtils.error(TAG, "error in retry retryTesHarian", it)
                    onInitTesFailed(it)
                }
            )
    }

    private fun initSwipeRefreshKelas() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                test_result_list_refresher.setColorSchemeResources(R.color.colorPrimary)
                RxSwipeRefreshLayout.refreshes(test_result_list_refresher)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                tesHarianPresenter.publishTesHarianRefreshListEvent()
            }
    }

    private fun initTesHarian() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                initLayoutManager()
                showLoadingView()
            }
            .observeOn(Schedulers.io())
            .flatMap {
                tesHarianPresenter
                    .loadTesHarian(idJadwalKelas, createNewTesHarian())
            }
            .filter { it.isNotEmpty() }
            .map { it.hasilTesHarian }
            .doOnNext { hasilTesHolder(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "success in initTesHarian")
                    onInitHasilTesSucceed()
                },
                {
                    LogUtils.error(TAG, "error in initTesHarian", it)
                    onInitTesFailed(it)
                }
            )
    }

    private fun createNewTesHarian(): TesHarian {
        val id = rand()
        val listHasilTesHarian = mutableListOf<HasilTesHarian>()
        val listSiswa: MutableList<Siswa> = tesHarianPresenter.getSiswa().toMutableList()
        listSiswa.forEach {
            listHasilTesHarian.add(HasilTesHarian(null, null, id, it.id))
        }
        return TesHarian(id, null, null, idJadwalKelas, listHasilTesHarian)
    }

    private fun rand(): Int {
        val random = Random()
        return random.nextInt(Int.MAX_VALUE - 1000) + 1000
    }

    private fun onInitTesFailed(throwable: Throwable) {
        hideAllViews()
        when (throwable) {
            is NetworkError -> showNetworkErrorView()
            is NoInternetError -> showNetworkErrorView()
            is ResultEmptyError -> {
                showResultEmptyErrorView()
            }
            else -> showUnknownErrorView()
        }
    }

    private fun onInitHasilTesSucceed() {
        hideAllViews()
        adapter.updateDataSet(list)
        showContent()
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        test_results_container.layoutManager = layoutManager
        test_results_container.setHasFixedSize(true)
        test_results_container.adapter = adapter
    }

    private fun hasilTesHolder(listHasilTes: MutableList<HasilTesHarian>) {
        list.clear()
        tempList.clear()
        tempList.addAll(listHasilTes)
        listHasilTes.forEach {
            list.add(HasilTesHolder(it, tesHarianPresenter))
        }
    }

    private fun launchEditActivity(hasilTesHarian: HasilTesHarian,
                                   nama: String) {
        val intent = Intent(this, HasilTesHarianAddOrEditActivity::class.java)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT, hasilTesHarian)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT_NAME, nama)
        startActivityForResult(intent, EDIT_REQUEST)
    }

    private fun showContent() {
        hideAllViews()
        list_wrapper.makeVisible()
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
        hideLoadingView()
        hideLoadProgress()
        list_wrapper.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        result_empty_error_view.makeGone()
    }

    private fun showLoadingView() {
        loading_view.makeVisible()
    }

    private fun hideLoadingView() {
        loading_view.makeGone()
    }

    private fun showLoadProgress() {
        test_result_list_refresher?.let { it.isRefreshing = true }
    }

    private fun hideLoadProgress() {
        test_result_list_refresher?.let { it.isRefreshing = false }
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

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is HasilTesHolder) {
            launchEditActivity(item.model, item.getNama() ?: "")
            return true
        }
        return false
    }

}
