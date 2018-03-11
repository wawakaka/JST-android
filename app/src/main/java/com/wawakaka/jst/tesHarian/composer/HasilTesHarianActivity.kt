package com.wawakaka.jst.tesHarian.composer

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.model.ActivityResult
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT_EDIT
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT_NAME
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import com.wawakaka.jst.tesHarian.model.TesHarian
import com.wawakaka.jst.tesHarian.presenter.TesHarianPresenter
import com.wawakaka.jst.tesHarian.view.HasilTesHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_hasil_tes_harian.*

class HasilTesHarianActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {
    //todo add up and back button checking dialog
    companion object {
        private val TAG = TesHarianActivity::class.java.simpleName
        private const val EDIT_REQUEST = 2081
    }

    private val tesHarianPresenter: TesHarianPresenter by lazy {
        JstApplication.component.provideTesHarianPresenter()
    }

    private var idJadwalKelas: Int? = null
    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)
    private var tempList: MutableList<HasilTesHarian> = mutableListOf()

    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initResultEmptyErrorView()
        initHasilTes()
        editNilaiResult()
        initSaveNilai()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                idJadwalKelas = intent.getSerializableExtra(TesHarianActivity.EXTRA_ID_JADWAL) as Int
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

    private fun initHasilTes() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                showLoadingView()
                initLayoutManager()
            }
            .observeOn(Schedulers.io())
            .map {
                tesHarianPresenter
                    .getTesHarian(idJadwalKelas ?: 0)
                    .hasilTesHarian
            }
            .doOnNext { hasilTesHolder(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                LogUtils.debug(TAG, "success in initHasilTes")
                showResultIfNotEmpty(it)
            }
    }

    private fun showResultIfNotEmpty(hasilTesHarian: MutableList<HasilTesHarian>?) {
        if (hasilTesHarian?.isEmpty() == true) {
            showResultEmptyErrorView()
        } else {
            onInitHasilTesSucced()
        }
    }

    private fun onInitHasilTesSucced() {
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

    private fun isAllScored(): Boolean {
        return tesHarianPresenter.getTesHarian(idJadwalKelas ?: 0).hasilTesHarian?.size == tesHarianPresenter.getSiswa().size
    }

    private fun launchEditActivity(hasilTesHarian: HasilTesHarian,
                                   nama: String) {
        val intent = Intent(this, HasilTesHarianAddOrEditActivity::class.java)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT, hasilTesHarian)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT_NAME, nama)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT_EDIT, true)
        startActivityForResult(intent, EDIT_REQUEST)
    }

    private fun updateTesHarian(tesHarian: TesHarian) {
        Observable
            .just(tesHarian)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                //todo show progress loading
            }
            .flatMap {
                tesHarianPresenter
                    .updateTesHarian(it, idJadwalKelas ?: 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in delete test result", it)
                        //todo dismiss loading
                        onUpdateTesHarianError(it)
                        Observable.just(false)
                    })
            }
            .filter { it }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in delete test result")
                    //todo dismiss loading
                },
                {
                    LogUtils.error(TAG, "Error in delete test result", it)
                    //todo dismiss loading
                    onUpdateTesHarianError(it)
                }
            )
    }

    private fun onUpdateTesHarianError(throwable: Throwable) {
        hideLoadingView()
        when (throwable) {
            is NetworkError -> showNetworkErrorView()
            is NoInternetError -> showNetworkErrorView()
            is ResultEmptyError -> showResultEmptyErrorView()
            else -> showUnknownErrorView()
        }
    }

    private fun showContent() {
        hideAllViews()
        list_wrapper.makeVisible()
        save_nilai.makeVisible()
    }

    private fun onAddHasilTestReceived(hasilTesHarian: HasilTesHarian) {
        list.add(HasilTesHolder(hasilTesHarian, tesHarianPresenter))
        onInitHasilTesSucced()
    }

    private fun editNilaiResult() {
        LogUtils.debug(TAG, "initEditNilaiResult")
        RxNavi
            .observe(naviComponent, Event.ACTIVITY_RESULT)
            .filter { isEditResultOk(it) }
            .map { getAddOrEditHasilTestResult(it) ?: HasilTesHarian.empty }
            .filter { it.isNotEmpty() }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "success in editNilaiResult")
                    onEditHasilTestReceived(it)
                },
                { LogUtils.error(TAG, "Error in editNilaiResult", it) }
            )
    }

    private fun isEditResultOk(result: ActivityResult): Boolean {
        return result.requestCode() == EDIT_REQUEST
            && result.resultCode() == Activity.RESULT_OK
    }

    private fun onEditHasilTestReceived(hasilTesHarian: HasilTesHarian) {
        tempList.forEach {
            if (it.id == hasilTesHarian.id) {
                it.hasil = hasilTesHarian.hasil
            }
        }
        (0 until list.size - 1).forEach { i ->
            val model = (list[i] as HasilTesHolder).model
            if (model.id == hasilTesHarian.id) {
                list.remove(list[i])
                list.add(HasilTesHolder(hasilTesHarian, tesHarianPresenter))
            }
        }
        onInitHasilTesSucced()
    }

    private fun initSaveNilai() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_nilai) }
            .filter { list.isNotEmpty() }
                .flatMap {
                    tesHarianPresenter.updateHasilTesHarian(
                            idJadwalKelas ?: 0,
                            tesHarianPresenter.getTesHarian(idJadwalKelas ?: 0).id ?: 0,
                            tempList
                    )
                }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            LogUtils.debug(TAG, "success in initSaveNilai")
                            tesHarianPresenter.publishTesHarianUpdateEvent()
                            finish()

                        },
                        { LogUtils.error(TAG, "Error in initSaveNilai", it) }
                )
    }

    private fun getAddOrEditHasilTestResult(result: ActivityResult): HasilTesHarian? {
        return result.data()?.getSerializableExtra(EXTRA_DAILY_TEST_RESULT) as HasilTesHarian
    }

    private fun showLoadingView() {
        loading_view.makeVisible()
    }

    private fun hideLoadingView() {
        loading_view.makeGone()
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
        save_nilai.makeGone()
        list_wrapper.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
        result_empty_error_view.makeGone()
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
