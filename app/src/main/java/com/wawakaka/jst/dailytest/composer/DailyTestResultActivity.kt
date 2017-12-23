package com.wawakaka.jst.dailytest.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.dailytest.composer.TestResultAddOrEditActivity.Companion.EXTRA_DAILY_TEST_ID
import com.wawakaka.jst.dailytest.composer.TestResultAddOrEditActivity.Companion.EXTRA_DAILY_TEST_RESULT
import com.wawakaka.jst.dailytest.model.HasilTesHarian
import com.wawakaka.jst.dailytest.model.TesHarian
import com.wawakaka.jst.dailytest.view.HasilTesHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_daily_test_result.*

class DailyTestResultActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    companion object {
        private val TAG = DailyTestActivity::class.java.simpleName
    }

    private val dailyTestPresenter by lazy {
        JstApplication.component.provideDailyTestPresenter()
    }

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initOnResume()
        initTestResults()
        initAddButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_daily_test_result)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initOnResume() {
        RxNavi
            .observe(naviComponent, Event.RESUME)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.computation())
            .map { dailyTestPresenter.getTesHarian().hasilTesHarian }
            .filter { it.isNotEmpty() }
            .doOnNext { hasilTesHolder(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adapter.updateDataSet(list)
            }
    }

    //todo add loading view

    private fun initTestResults() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.computation())
            .map { dailyTestPresenter.getTesHarian().hasilTesHarian }
            .filter { it.isNotEmpty() }
            .doOnNext { hasilTesHolder(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adapter.updateDataSet(list)
            }
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        test_results_container.layoutManager = layoutManager
        test_results_container.setHasFixedSize(true)
        test_results_container.adapter = adapter
    }

    private fun hasilTesHolder(listHasilTes: MutableList<HasilTesHarian>) {
        list.clear()
        listHasilTes.map { list.add(HasilTesHolder(it, dailyTestPresenter)) }
    }

    private fun initAddButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(fab_add) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchAddActivity()
            }
    }

    //todo add loading view
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
            launchEditActivity(item.model)
            return true
        }
        return false
    }

    override fun onItemLongClick(position: Int) {
        val item = adapter.getItem(position)
        if (item is HasilTesHolder) {
            launchDeleteConfirmationDialog(item.model)
        }
    }

    private fun launchEditActivity(hasilTesHarian: HasilTesHarian) {
        val intent = Intent(this, TestResultAddOrEditActivity::class.java)
        intent.putExtra(EXTRA_DAILY_TEST_RESULT, hasilTesHarian)
        startActivity(intent)
    }

    private fun launchAddActivity() {
        val intent = Intent(this, TestResultAddOrEditActivity::class.java)
        intent.putExtra(EXTRA_DAILY_TEST_ID, dailyTestPresenter.getTesHarian().id)
    }

    private fun launchDeleteConfirmationDialog(hasilTesHarian: HasilTesHarian) {
        //todo create delete dialog
        ViewUtils
            .showConfirmationObservable(
                this,
                getString(R.string.daily_delete_dialog_title),
                getString(R.string.daily_delete_dialog_message)
            )
            .filter { it }
            .subscribe {
                var testHarian = dailyTestPresenter.getTesHarian()
                var hasilTesHarianList = dailyTestPresenter.getTesHarian().hasilTesHarian
                hasilTesHarianList?.remove(hasilTesHarianList?.find { it.id == hasilTesHarian.id })
                testHarian.hasilTesHarian = hasilTesHarianList
                updateTesHarian(testHarian)
            }
    }

    private fun updateTesHarian(tesHarian: TesHarian) {
        Observable
            .just(tesHarian)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                //todo show progress loading
            }
            .flatMap {
                dailyTestPresenter
                    .updateTesHarian(it)
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
        //todo handle error
    }

}
