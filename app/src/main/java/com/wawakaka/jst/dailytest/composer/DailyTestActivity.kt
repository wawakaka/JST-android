package com.wawakaka.jst.dailytest.composer

import android.content.Intent
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.dailytest.model.TesHarian
import com.wawakaka.jst.dailytest.presenter.DailyTestPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_daily_test.*

class DailyTestActivity : BaseActivity() {

    companion object {
        private val TAG = DailyTestActivity::class.java.simpleName
        const val EXTRA_ID_KELAS = "extra_id_kelas"
        const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    private var idJadwalKelas: Int? = null
    private var idKelas: Int? = null

    private val dailyTestPresenter: DailyTestPresenter by lazy {
        JstApplication.component.provideDailyTestPresenter()
    }

    //todo add error screen
    init {
        initLayout()
        initUploadButton()
        initDownloadButton()
        initViewResultButton()
        initDailyTest()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                idJadwalKelas = intent.getSerializableExtra(EXTRA_ID_JADWAL) as Int
                idKelas = intent.getSerializableExtra(EXTRA_ID_KELAS) as Int
                setContentView(R.layout.activity_daily_test)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUploadButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(daily_test_upload_button) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                //todo do something
            }
    }

    private fun initDownloadButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(daily_test_download_button) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                //todo do something
            }
    }

    private fun initViewResultButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(daily_test_view_result_button) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchTesResultActivity()
            }
    }

    private fun launchTesResultActivity() {
        val intent = Intent(this, DailyTestResultActivity::class.java)
        startActivity(intent)
    }

    private fun initDailyTest() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            //todo show loading
            .observeOn(Schedulers.io())
            .flatMap {
                dailyTestPresenter
                    .loadTesHarian(idJadwalKelas!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in initDailyTest", it)
                        onLoadDailyTestError(it)
                        Observable.just(TesHarian.empty)
                    })
            }
            .filter { !it.isEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in initDailyTest")
                    //todo dismiss loading
                },
                {
                    LogUtils.error(TAG, "Error in initDailyTest", it)
                    onLoadDailyTestError(it)
                }
            )
    }

    private fun onLoadDailyTestError(throwable: Throwable) {
//        hideProgressDialog()
        //todo add all strings to resource
//        when (throwable) {
//            is NetworkError -> showError("error network")
//            is NoInternetError -> showError("no internet error")
//            else -> showError("unknown error")
//        }
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
}
