package com.wawakaka.jst.admin.composer

import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.admin.utils.ExtraUtils.Companion.IS_EDIT
import com.wawakaka.jst.admin.utils.ExtraUtils.Companion.NAMA_BIDANG
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_bidang.*

class AddOrEditBidangActivity : BaseActivity() {

    companion object {
        val TAG = AddOrEditBidangActivity::class.java.simpleName!!
    }

    private val nama: String? by lazy {
        intent.getSerializableExtra(NAMA_BIDANG) as? String
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(IS_EDIT) as? Boolean
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null

    init {
        initLayout()
        initNamaBidang()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_add_or_edit_bidang)
                    initToolbar()
                }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNamaBidang() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { setNamaBidang() }
    }

    private fun setNamaBidang() {
        nama_text.setText(nama)
    }

    private fun initSaveButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(save_button) }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap { adminPresenter.addBidangObservable(Bidang(getNamaBidang())) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            onSaveBidangSucceed()
                        },
                        {
                            LogUtils.error(TAG, "error in initSaveButton", it)
                            onSaveBidangError(it)
                        }
                )
    }

    private fun onSaveBidangSucceed() {
        hideProgressDialog()
        finish()
    }

    private fun onSaveBidangError(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> {
                showSnackbarError(getString(R.string.error_network))

            }
            is NoInternetError -> {
                showSnackbarError(getString(R.string.error_no_internet))

            }
            else -> {
                showSnackbarError(getString(R.string.error_unknown))

            }
        }
    }

    private fun showSnackbarError(errorString: String) {
        ViewUtils.showSnackbarError(findViewById(android.R.id.content), errorString)
    }

    private fun getNamaBidang(): String {
        return nama_text.text.toString()
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
