package com.wawakaka.jst.admin.kelas.composer

import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.bidang.composer.AddOrEditBidangActivity
import com.wawakaka.jst.admin.event.model.EventSpinnerItem
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.base.utils.ExtraUtils
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
    private val listEvent: MutableList<String> = mutableListOf()
    private var listSpinnerItem: MutableList<EventSpinnerItem> = mutableListOf()

    init {
        initLayout()
        initUserSpinner()
        initBidangSpinner()
        initEventSpinner()
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
                        LogUtils.error(AddOrEditKelasActivity.TAG, "Error in adminPresenter.loadAllUser", it)

                        Observable.just(mutableListOf())
                    })
            }
            .filter { it.isNotEmpty() }
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

    private fun populateBidangSpinner(bidang: MutableList<Bidang>) {
        listBidang.clear()
        listBidang.add("")
        bidang.forEach {
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

    private fun initEventSpinner() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap {
                adminPresenter
                    .loadAllEvent()
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in adminPresenter.loadAllEvent", it)

                        Observable.just(mutableListOf())
                    })
            }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { populateEventSpinner(it) }
    }

    private fun populateEventSpinner(event: MutableList<com.wawakaka.jst.event.model.Event>) {
        listEvent.clear()
        listSpinnerItem.clear()
        listEvent.add("")
        listSpinnerItem.add(EventSpinnerItem(0, ""))
        event.forEach {
            listEvent.add(
                it.sekolahNama +
                    " " +
                    DateUtils.getShortDate(it.tanggalMulai ?: "") +
                    "-" +
                    DateUtils.getShortDate(it.tanggalSelesai ?: "")
            )
            listSpinnerItem.add(
                EventSpinnerItem(it.id,
                    it.sekolahNama +
                        " " +
                        DateUtils.getShortDate(it.tanggalMulai ?: "") +
                        "-" +
                        DateUtils.getShortDate(it.tanggalSelesai ?: "")
                )
            )
        }

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            listEvent
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        event_spinner.adapter = adapter
        LogUtils.debug("ketemu", listSpinnerItem.find {
            it.id == kelas?.eventId
        }?.text.toString())
        if (isEdit == true) {
            val position = adapter.getPosition(
                listEvent.find {
                    it == listSpinnerItem.find {
                        it.id == kelas?.eventId
                    }?.text
                }
            )
            LogUtils.debug("posisinya", position.toString())

            event_spinner.setSelection(position)
        }
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
            .observeOn(AndroidSchedulers.mainThread())
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
        val bidang = bidang_spinner.selectedItem.toString().isBlank()
        if (user) {
            user_error.makeVisible()
        }
        if (bidang) {
            bidang_error.makeVisible()
        }
        return (user || bidang)
    }

    private fun composeKelas(): Kelas {
        val email = adminPresenter.getUsers().find { it.nama == user_spinner.selectedItem.toString() }
        return Kelas(
            kelas?.id,
            true,
            bidang_spinner.selectedItem.toString(),
            email?.email,
            kelas?.jadwalKelas,
            kelas?.listSiswa,
            listSpinnerItem.find { it.text == event_spinner.selectedItem.toString() }?.id
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
                showSnackbarError(getString(R.string.error_unknown))
            }
            else -> {
                showSnackbarError(getString(R.string.error_unknown))

            }
        }
    }

    private fun hideAllError() {
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
