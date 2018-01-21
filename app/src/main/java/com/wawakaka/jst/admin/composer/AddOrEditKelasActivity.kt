package com.wawakaka.jst.admin.composer

import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.model.Sekolah
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.admin.utils.ExtraUtils
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Bidang
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_kelas.*

class AddOrEditKelasActivity : BaseActivity() {

    companion object {
        val TAG = AddOrEditBidangActivity::class.java.simpleName!!
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as? Boolean
    }

    private val kelas: Kelas? by lazy {
        intent.getSerializableExtra(ExtraUtils.KELAS) as? Kelas
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null
    private val listUser: MutableList<String> = mutableListOf()
    private val listBidang: MutableList<String> = mutableListOf()
    private val listSekolah: MutableList<String> = mutableListOf()

    init {
        initLayout()
        initUserSpinner()
        initBidangSpinner()
        initSekolahSpinner()
        initKelasStatus()
        initIsPrivateKelas()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_add_or_edit_kelas)
                    initToolbar()

                    LogUtils.debug("isEdit", isEdit.toString())
                }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUserSpinner() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap {
                    adminPresenter
                            .loadAllUser()
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(Function {
                                LogUtils.error(TAG, "Error in adminPresenter.loadAllUser", it)

                                Observable.just(mutableListOf())
                            })
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { populateUserSpinner(it) }
    }

    private fun populateUserSpinner(user: MutableList<User>) {
        listUser.clear()
        listUser.add("")
        user.forEach {
            listUser.add(it.nama ?: "")
        }
        val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                listUser
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        user_spinner.adapter = adapter
        if (isEdit == true) {
            val user = adminPresenter.getUsers().find { it.email == kelas?.userEmail ?: "" }?.nama
            val position = adapter.getPosition(user)
            user_spinner.setSelection(position)
        }
    }

    private fun initBidangSpinner() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap {
                    adminPresenter
                            .loadAllBidangObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(Function {
                                LogUtils.error(TAG, "Error in adminPresenter.loadAllBidangObservable", it)

                                Observable.just(mutableListOf())
                            })
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { populateBidangSpinner(it) }
    }

    private fun populateBidangSpinner(sekolah: MutableList<Bidang>) {
        listBidang.clear()
        listBidang.add("")
        sekolah.forEach {
            listBidang.add(it.nama ?: "")
        }
        val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                listBidang
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bidang_spinner.adapter = adapter
        if (isEdit == true) {
            val bidang = adminPresenter.getBidang().find { it.nama == kelas?.bidangNama ?: "" }?.nama
            val position = adapter.getPosition(bidang)
            bidang_spinner.setSelection(position)
        }
    }

    private fun initSekolahSpinner() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap {
                    adminPresenter
                            .loadAllSekolah()
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(Function {
                                LogUtils.error(TAG, "Error in adminPresenter.loadAllSekolah", it)

                                Observable.just(mutableListOf())
                            })
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { populateSekolahSpinner(it) }
    }

    private fun populateSekolahSpinner(sekolah: MutableList<Sekolah>) {
        listSekolah.clear()
        listSekolah.add("")
        sekolah.forEach {
            listSekolah.add(it.nama ?: "")
        }
        val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                listSekolah
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sekolah_spinner.adapter = adapter
        if (isEdit == true) {
            val sekolah = adminPresenter.getSekolah().find { it.nama == kelas?.sekolahNama ?: "" }?.nama
            val position = adapter.getPosition(sekolah)
            sekolah_spinner.setSelection(position)
        }
    }

    private fun initKelasStatus() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { is_kelas_active.isChecked = kelas?.isActive ?: false }
    }

    private fun initIsPrivateKelas() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { is_kelas_private.isChecked = kelas?.isPrivate ?: false }
    }

    private fun initSaveButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(save_button) }
                .map { isErrorShown() }
                .filter { !it }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    if (isEdit == true) {
                        adminPresenter.updateKelasObservable(kelas?.id ?: 0, composeKelas())
                    } else {
                        adminPresenter.addKelasObservable(composeKelas())
                    }
                }
                .filter { it }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            onSaveKelasSucceed()
                        },
                        {
                            LogUtils.error(TAG, "error in initSaveButton", it)
                            onSaveKelasError(it)
                        }
                )
    }

    private fun isErrorShown(): Boolean {
        hideAllError()
        val user = user_spinner.selectedItem.toString().isBlank()
        val sekolah = sekolah_spinner.selectedItem.toString().isBlank()
        val bidang = bidang_spinner.selectedItem.toString().isBlank()
        if (user) {
            user_error.makeVisible()
        }
        if (sekolah) {
            sekolah_error.makeVisible()
        }
        if (bidang) {
            bidang_error.makeVisible()
        }
        return (user || sekolah || bidang)
    }

    private fun composeKelas(): Kelas {
        val email = adminPresenter.getUsers().find { it.nama == user_spinner.selectedItem.toString() }
        return Kelas(
                kelas?.id,
                is_kelas_private.isChecked,
                is_kelas_active.isChecked,
                bidang_spinner.selectedItem.toString(),
                sekolah_spinner.selectedItem.toString(),
                email?.email,
                kelas?.jadwalKelas,
                kelas?.listSiswa
        )
    }

    private fun onSaveKelasSucceed() {
        hideProgressDialog()
        adminPresenter.publishRefreshListKelasEvent()
        finish()
    }

    private fun onSaveKelasError(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> {
                showSnackbarError(getString(R.string.error_network))

            }
            is NoInternetError -> {
                showSnackbarError(getString(R.string.error_no_internet))

            }
            is ResultEmptyError -> {
                //todo do something or leave it blank
            }
            else -> {
                showSnackbarError(getString(R.string.error_unknown))

            }
        }
    }

    private fun hideAllError() {
        sekolah_error.makeGone()
        bidang_error.makeGone()
        user_error.makeGone()
    }

    private fun showSnackbarError(errorString: String) {
        ViewUtils.showSnackbarError(findViewById(android.R.id.content), errorString)
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
