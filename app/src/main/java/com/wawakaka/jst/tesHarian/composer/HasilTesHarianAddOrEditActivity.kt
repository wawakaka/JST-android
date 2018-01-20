package com.wawakaka.jst.tesHarian.composer

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_test_result_add_or_edit.*


class HasilTesHarianAddOrEditActivity : BaseActivity() {
    //todo add field checking function
    companion object {
        private val TAG = HasilTesHarianAddOrEditActivity::class.java.simpleName
        const val EXTRA_DAILY_TEST_RESULT = "daily-test-result"
        const val EXTRA_DAILY_TEST_RESULT_NAME = "nama-daily-test-result"
        const val EXTRA_DAILY_TEST_RESULT_EDIT = "edit"
        const val EXTRA_ID_JADWAL_KELAS = "id-jadwal-kelas"
    }

    private val hasilTesHarian: HasilTesHarian? by lazy {
        intent.getSerializableExtra(EXTRA_DAILY_TEST_RESULT) as? HasilTesHarian
    }

    private val nama: String? by lazy {
        intent.getSerializableExtra(EXTRA_DAILY_TEST_RESULT_NAME) as? String
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(EXTRA_DAILY_TEST_RESULT_EDIT) as? Boolean
    }

    init {
        initLayout()
        initData()
        initAddButton()
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
            .map { isEdit }
            .filter { it }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                showDataIfInEditMode()
            }
    }

    private fun showDataIfInEditMode() {
        nama_siswa.text = nama
        nilai.setText(hasilTesHarian?.hasil)
    }

    private fun initAddButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_button) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { setResultSuccess(getNilai()) }
    }

    private fun getNilai(): HasilTesHarian {
        return HasilTesHarian(hasilTesHarian?.id, nilai.text.toString(), hasilTesHarian?.tesHarianId, hasilTesHarian?.siswaId)
    }

    private fun setResultSuccess(hasilTesHarian: HasilTesHarian) {
        val data = Intent()
        LogUtils.debug(TAG, hasilTesHarian.toString())
        data.putExtra(EXTRA_DAILY_TEST_RESULT, hasilTesHarian)
        setResult(Activity.RESULT_OK, data)
        finish()
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
            showUnsavedConfirmationDialog {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        showUnsavedConfirmationDialog {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

}
