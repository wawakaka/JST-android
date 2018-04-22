package com.wawakaka.jst.admin.siswa.composer

import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_siswa.*

class AddOrEditSiswaActivity : BaseActivity() {

    companion object {
        private val TAG = AddOrEditSiswaActivity::class.java.simpleName
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as? Boolean
    }

    private val siswa: Siswa? by lazy {
        intent.getSerializableExtra(ExtraUtils.SISWA) as? Siswa
    }

    private var progressDialog: DefaultProgressDialog? = null
    private val listSekolah: MutableList<String> = mutableListOf()
    private val listKelas: MutableList<String> = mutableListOf()

    init {
        initLayout()
        initNamaSiswa()
        initSekolahSpinner()
        initKelas()
        initStatus()
        initKelasSpinner()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_add_or_edit_siswa)
                    initToolbar()
                }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNamaSiswa() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    nama_text.setText(siswa?.nama ?: "")
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
            val sekolah = adminPresenter.getSekolah().find { it.nama == siswa?.sekolahNama ?: "" }?.nama
            val position = adapter.getPosition(sekolah)
            sekolah_spinner.setSelection(position)
        }
    }

    private fun initKelas() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    kelas_sekolah_text.setText(siswa?.kelas ?: "")
                }
    }

    private fun initStatus() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    is_siswa_active.isChecked = siswa?.isActive ?: false
                }
    }

    private fun initKelasSpinner() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap {
                    adminPresenter
                            .loadAllKelasObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(Function {
                                LogUtils.error(TAG, "Error in adminPresenter.loadAllKelasObservable", it)

                                Observable.just(mutableListOf())
                            })
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { populateKelasSpinner(it) }
    }

    private fun populateKelasSpinner(kelas: MutableList<Kelas>) {
        listKelas.clear()
        listKelas.add("")
        kelas.forEach {
            listKelas.add(it.id.toString())
        }
        val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                listKelas
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kelas_spinner.adapter = adapter
        if (isEdit == true) {
            val kelas = adminPresenter.getKelas().find { it.id == siswa?.kelaId ?: "" }?.id.toString()
            val position = adapter.getPosition(kelas)
            kelas_spinner.setSelection(position)
        }
    }

    private fun initSaveButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(save_button) }
                .map { isValidInput() }
                .filter { it }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    if (isEdit == true) {
                        adminPresenter.updateSiswaObservable(composeSiswa())
                    } else {
                        adminPresenter.addSiswaObservable(composeSiswa())
                    }
                }
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            onSaveSiswaSucced()
                        },
                        {
                            LogUtils.error(TAG, "error in initSaveButton", it)
                            onSaveKelasError(it)
                        }
                )
    }

    private fun isValidInput(): Boolean {
        val nama = isValidName()
        val sekolah = isValidSekolah()
        val kelas = isValidKelas()
        return nama && sekolah && kelas
    }

    private fun isValidName(): Boolean {
        return if (nama_text.text.isNotBlank()) {
            nama_hint.isErrorEnabled = false
            nama_hint.error = null
            true
        } else {
            nama_hint.isErrorEnabled = true
            nama_hint.error = getString(R.string.add_or_edit_nama_error)
            false
        }
    }

    private fun isValidSekolah(): Boolean {
        return if (sekolah_spinner.selectedItem.toString().isBlank()) {
            sekolah_error.makeVisible()
            false
        } else {
            sekolah_error.makeGone()
            true
        }
    }

    private fun isValidKelas(): Boolean {
        return if (kelas_spinner.selectedItem.toString().isBlank()) {
            kelas_error.makeVisible()
            false
        } else {
            kelas_error.makeGone()
            true
        }
    }

    //    todo temporary using id kelas in kelas spinner
    private fun composeSiswa(): Siswa {
        return Siswa(
                siswa?.id,
                nama_text.text.toString(),
                kelas_sekolah_text.text.toString(),
                is_siswa_active.isChecked,
                kelas_spinner.selectedItem.toString().toInt(),
                sekolah_spinner.selectedItem.toString(),
                siswa?.hasilTesHarian,
                siswa?.laporanAkhir
        )
    }

    private fun onSaveSiswaSucced() {
        hideProgressDialog()
        adminPresenter.publishRefreshListSiswaEvent()
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
