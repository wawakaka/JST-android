package com.wawakaka.jst.presensi.composer

import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.kelas.composer.KelasActivity
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.presensi.model.PresensiRequestWrapper
import com.wawakaka.jst.presensi.presenter.PresensiPresenter
import com.wawakaka.jst.presensi.view.PresensiCheckView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_presensi.*
import org.jetbrains.anko.forEachChild

class PresensiActivity : BaseActivity() {

    companion object {
        private val TAG = PresensiActivity::class.java.simpleName
    }

    private var kelas: Kelas? = null
    private var idJadwalKelas: Int? = null

    private val presensiPresenter: PresensiPresenter by lazy {
        JstApplication.component.providePresensiPresenter()
    }

    init {
        initLayout()
        initUnknownErrorView()
        initNetworkErrorView()
        initListSiswa()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                kelas = intent.getSerializableExtra(KelasActivity.EXTRA_KELAS) as Kelas
                idJadwalKelas = intent.getSerializableExtra(KelasActivity.EXTRA_ID_JADWAL) as Int
                setContentView(R.layout.activity_presensi)
                initToolbar()
            }
    }

    private fun initNetworkErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                network_error_view.setActionOnClickListener(View.OnClickListener {
                    network_error_view.isEnabled = false
                    initCheckedList()
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
                    initCheckedList()
                })
            }
    }

    private fun initListSiswa() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showLoadingView() }
            .observeOn(Schedulers.computation())
            .map { kelas?.listSiswa ?: mutableListOf() }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in load list siswa")
                    onLoadListSiswaSucceded(it)
                },
                {
                    LogUtils.error(TAG, "Error in load list siswa", it)
                    onLoadListSiswaFailed(it)
                }
            )
    }

    private fun onLoadListSiswaSucceded(listSiswa: MutableList<Siswa>) {
        listSiswa.forEach { addSiswa(it) }
        initCheckedList()
    }

    private fun initCheckedList() {
        Observable
            .just(true)
            .observeOn(Schedulers.io())
            .flatMap { presensiPresenter.loadPresensiCheckedListObservable(idJadwalKelas) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in initCheckedList presensi")
                    setCheckedSiswa(it)
                    showSiswaListContainer()
                },
                {
                    LogUtils.error(TAG, "Error in initCheckedList presensi", it)
                    onLoadListSiswaFailed(it)
                }
            )
    }

    private fun setCheckedSiswa(listSiswa: MutableList<Presensi>) {
        siswa_list_content
            .forEachChild { presensi ->
                if (presensi is PresensiCheckView) {
                    val daftarAbsen = presensi.getSiswa()
                    listSiswa.forEach { siswa ->
                        if (daftarAbsen.id == siswa.siswaId) {
                            presensi.setSiswaAttend()
                        }
                    }
                }
            }
    }

    private fun addSiswa(siswa: Siswa) {
        val defaultCheckView = buildDefaultCheckView()
        siswa_list_content.addView(defaultCheckView, 0)

        defaultCheckView.post {
            defaultCheckView.setSiswa(siswa)
        }
    }

    private fun buildDefaultCheckView() = PresensiCheckView(this)

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { save_presensi.setEnabledState(true) }
            .flatMap { RxView.clicks(save_presensi) }
            .doOnNext { showProgressDialog() }
            .map { setPresensi() }
            .doOnNext { LogUtils.debug("list presensi", it.presensi.toString()) }
            .observeOn(Schedulers.io())
            .flatMap {
                presensiPresenter
                    .savePresensiCheckedListObservable(
                        idJadwalKelas,
                        it
                    )
            }
            .filter { it }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in initSaveButton")
                    hideProgressDialog()
                    showSiswaListContainer()
                    finish()
                },
                {
                    LogUtils.error(TAG, "Error in initSaveButton", it)
                    hideProgressDialog()
                    showSiswaListContainer()
                }
            )
    }

    private fun setPresensi(): PresensiRequestWrapper {
        val presensi = mutableListOf<Presensi>()
        siswa_list_content
            .forEachChild {
                if (it is PresensiCheckView) {
                    if (it.isSiswaAttend()) {
                        presensi.add(it.getPresensi(idJadwalKelas!!))
                    }
                }
            }
        return PresensiRequestWrapper(presensi)
    }

    private fun onLoadListSiswaFailed(throwable: Throwable) {
        hideAllViews()
        when (throwable) {
            is NetworkError -> showNetworkErrorView()
            is NoInternetError -> showNetworkErrorView()
            is ResultEmptyError -> showSiswaListContainer()
            else -> showUnknownErrorView()
        }
    }

    private fun showSiswaListContainer() {
        hideAllViews()
        content_container.makeVisible()
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

    private fun showLoadingView() {
        hideAllViews()
        loading_view.makeVisible()
    }

    private fun showProgressDialog() {
        save_presensi.enableLoadingState()
    }

    private fun hideProgressDialog() {
        save_presensi.disableLoadingState()
    }


    private fun hideAllViews() {
        loading_view.makeGone()
        content_container.makeGone()
        network_error_view.makeGone()
        unknown_error_view.makeGone()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
