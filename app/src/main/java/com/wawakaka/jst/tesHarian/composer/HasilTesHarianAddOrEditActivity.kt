package com.wawakaka.jst.tesHarian.composer

import android.app.Activity
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import com.wawakaka.jst.tesHarian.presenter.TesHarianPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_test_result_add_or_edit.*


class HasilTesHarianAddOrEditActivity : BaseActivity() {

    companion object {
        private val TAG = HasilTesHarianAddOrEditActivity::class.java.simpleName
        const val EXTRA_DAILY_TEST_RESULT = "daily-test-result"
        const val EXTRA_DAILY_TEST_RESULT_NAME = "nama-daily-test-result"
    }

    private val tesHarianPresenter: TesHarianPresenter by lazy {
        JstApplication.component.provideTesHarianPresenter()
    }

    private val hasilTesHarian: HasilTesHarian? by lazy {
        intent.getSerializableExtra(EXTRA_DAILY_TEST_RESULT) as? HasilTesHarian
    }

    private val nama: String? by lazy {
        intent.getSerializableExtra(EXTRA_DAILY_TEST_RESULT_NAME) as? String
    }

    init {
        initLayout()
        initData()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_test_result_add_or_edit)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                showData()
            }
    }

    private fun showData() {
        nama_siswa.text = nama
        nilai.setText(hasilTesHarian?.hasil)
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_button) }
            .flatMap {
                tesHarianPresenter
                    .updateHasilTesHarian(getNilai())
            }
            .filter { it }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "success in initAddButton")
                    tesHarianPresenter.publishTesHarianRefreshListEvent()
                    finish()
                },
                {
                    LogUtils.error(TAG, "Error in initAddButton", it)
                }
            )
    }

    private fun getNilai(): HasilTesHarian {
        return HasilTesHarian(hasilTesHarian?.id, nilai.text.toString(), hasilTesHarian?.tesHarianId, hasilTesHarian?.siswaId)
    }

    private fun showUnsavedConfirmationDialog(confirmedAction: () -> Unit) {
        ViewUtils.showUnsavedConfirmationDialog(this)
            .subscribeOn(AndroidSchedulers.mainThread())
            .filter { confirmed -> confirmed }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                confirmedAction()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return if (id == android.R.id.home) {
            if (isAnyChanges()) {
                showUnsavedConfirmationDialog {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (isAnyChanges()) {
            showUnsavedConfirmationDialog {
                setResult(Activity.RESULT_CANCELED)
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun isAnyChanges(): Boolean = nilai.text.toString() != hasilTesHarian?.hasil

}
