package com.wawakaka.jst.presensi.composer

import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultCheckView
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.kelas.composer.KelasActivity
import com.wawakaka.jst.presensi.model.Presensi
import com.wawakaka.jst.presensi.presenter.PresensiPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_presensi.*
import org.jetbrains.anko.forEachChild

class PresensiActivity : BaseActivity() {

    companion object {
        private val TAG = PresensiActivity::class.java.simpleName
        const val EXTRA_KELAS = "extra_kelas"
        const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    private var kelas: Kelas? = null
    private var idJadwalKelas: Int? = null

    private val presensiPresenter: PresensiPresenter by lazy {
        JstApplication.component.providePresensiPresenter()
    }

    init {
        initLayout()
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
                LogUtils.debug("idJadwalKelas", "$idJadwalKelas")
            }
    }

    private fun initListSiswa() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
//todo            .doOnNext { showLoadingView() }
            .observeOn(Schedulers.computation())
            .doOnNext { LogUtils.debug(TAG, "$kelas?.listSiswa") }
            .map { kelas?.listSiswa ?: mutableListOf() }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in load list siswa")
                    LogUtils.debug(TAG, "$it")
                    onLoadListSiswaSucceded(it)
                },
                {
                    LogUtils.error(TAG, "Error in load list siswa", it)
                    onLoadListSiswaFailed(it)
                }
            )
    }

    private fun onLoadListSiswaSucceded(listSiswa: MutableList<Siswa>) {
        showSiswaListContainer()
        listSiswa.forEach { addSiswa(it) }
        initCheckedList()
    }

    private fun initCheckedList() {
        Observable
            .just(true)
            .observeOn(Schedulers.io())
            .flatMap {
                presensiPresenter
                    .loadPresensiCheckedListObservable(idJadwalKelas)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in initCheckedList presensi", it)
                        Observable.just(null)
                        //todo dissable loading here

                    })
            }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in initCheckedList presensi")
                    LogUtils.debug(TAG, "$it")
                    setCheckedSiswa(it)
                },
                {
                    LogUtils.error(TAG, "Error in initCheckedList presensi", it)
                    //todo dissable loading here

                }
            )
    }

    private fun setCheckedSiswa(listSiswa: MutableList<Presensi>) {
        siswa_list_content
            .forEachChild { presensi ->
                if (presensi is DefaultCheckView) {
                    val daftarAbsen = presensi.getSiswa()
                    listSiswa.forEach { siswa ->
                        if (daftarAbsen.id == siswa.siswaId) {
                            presensi.setSiswaAttend()
                        }
                    }
                }
            }
        //todo dissable loading here

    }

    private fun addSiswa(siswa: Siswa) {
        val defaultCheckView = buildDefaultCheckView()
        siswa_list_content.addView(defaultCheckView, 0)

        defaultCheckView.post {
            defaultCheckView.setSiswa(siswa)
        }
    }

    private fun buildDefaultCheckView() = DefaultCheckView(this)

    private fun showSiswaListContainer() {
        hideAllViews()
        siswa_list_content.makeVisible()
    }

    private fun onLoadListSiswaFailed(throwable: Throwable) {
        when (throwable) {
        //todo add error screen
//            is NetworkError -> showNetworkErrorView()
//            is NoInternetError -> showNetworkErrorView()
//            is ResultEmptyError -> showEmptyShippingProvider()
//            else -> showUnknownErrorView()
        }
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_presensi) }
            .map { setPresensi() }
            .observeOn(Schedulers.io())
            .flatMap {
                presensiPresenter
                    .savePresensiCheckedListObservable(
                        idJadwalKelas,
                        it
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in initSaveButton", it)
                        Observable.just(null)
                        //todo dissable loading here

                    })
            }
            .filter { it }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "Success in initSaveButton")
                    //todo dissable loading here
                },
                {
                    LogUtils.error(TAG, "Error in initSaveButton", it)
                    //todo dissable loading here
                }
            )
    }

    private fun setPresensi(): MutableList<Presensi> {
        val presensi = mutableListOf<Presensi>()
        siswa_list_content
            .forEachChild {
                if (it is DefaultCheckView) {
                    presensi.add(it.getPresensi(idJadwalKelas!!))
                }
            }
        return presensi
    }

    private fun hideAllViews() {
        //todo manage view visibility
//        shipping_provider_content.makeGone()
//        network_error_view.makeGone()
//        unknown_error_view.makeGone()
//        loading_view.makeGone()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
