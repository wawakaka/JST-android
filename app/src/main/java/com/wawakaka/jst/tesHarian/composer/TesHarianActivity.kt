package com.wawakaka.jst.tesHarian.composer

import android.content.Intent
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
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import com.wawakaka.jst.tesHarian.model.TesHarian
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tes_harian.*
import java.util.*

class TesHarianActivity : BaseActivity() {

    companion object {
        private val TAG = TesHarianActivity::class.java.simpleName
        const val EXTRA_ID_KELAS = "extra_id_kelas"
        const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    private val tesHarianPresenter by lazy {
        JstApplication.component.provideTesHarianPresenter()
    }

    private var idJadwalKelas: Int? = null
    private var idKelas: Int? = null
    private val random = Random()


    init {
        initLayout()
        initNetworkErrorView()
        initUnknownErrorView()
        initUploadButton()
        initDownloadButton()
        initViewResultButton()
        initTesHarian()
        initListenToUpdateTesHarianEvent()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                idJadwalKelas = intent.getSerializableExtra(EXTRA_ID_JADWAL) as Int
                idKelas = intent.getSerializableExtra(EXTRA_ID_KELAS) as Int
                setContentView(R.layout.activity_tes_harian)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNetworkErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                network_error_view.setActionOnClickListener(View.OnClickListener {
                    network_error_view.isEnabled = false
                    reloadTesHarian()
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
                    reloadTesHarian()
                })
            }
    }

    private fun reloadTesHarian() {
        Observable
                .just(true)
                .doOnNext {
                    showLoadingView()
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    tesHarianPresenter
                            .reloadTesHarian(idJadwalKelas ?: 0)
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(Function {
                                LogUtils.error(TAG, "error in reloadTesHarian", it)
                                onInitTesFailed(it)
                                Observable.just(null)
                            })
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe(
                        {
                            LogUtils.debug(TAG, "success in reloadTesHarian")
                            onInitTesHarianSucceed(it)
                        },
                        {
                            LogUtils.error(TAG, "error in reloadTesHarian", it)
                            onInitTesFailed(it)
                        }
                )
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
        val intent = Intent(this, HasilTesHarianActivity::class.java)
        intent.putExtra(EXTRA_ID_JADWAL, idJadwalKelas)
        startActivity(intent)
    }

    private fun initTesHarian() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                showLoadingView()
            }
            .observeOn(Schedulers.io())
            .flatMap {
                tesHarianPresenter
                    .loadTesHarian(
                        idJadwalKelas ?: 0,
                        createNewTesHarian()
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "error in initTesHarian", it)
                        onInitTesFailed(it)
                        Observable.just(null)
                    })
            }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "success in initTesHarian")
                    onInitTesHarianSucceed(it)
                },
                {
                    LogUtils.error(TAG, "error in initTesHarian", it)
                    onInitTesFailed(it)
                }
            )
    }

    //todo set next activity to support this new method
    private fun createNewTesHarian(): TesHarian {
        val id = rand()
        val listHasilTesHarian = mutableListOf<HasilTesHarian>()
        val listSiswa: MutableList<Siswa> = tesHarianPresenter.getSiswa().toMutableList()
        listSiswa.forEach {
            listHasilTesHarian.add(HasilTesHarian(null, null, id, it.id))
        }
        return TesHarian(id, null, null, idJadwalKelas, listHasilTesHarian)
    }

    fun rand(): Int {
        return random.nextInt(Int.MAX_VALUE - 1000) + 1000
    }

    private fun onInitTesHarianSucceed(tesHarian: TesHarian) {
        hideLoadingView()
        setFileName(tesHarian)
    }

    private fun setFileName(tesHarian: TesHarian) {
        //todo handle checking file name and downloading
        if (tesHarian.file.isNullOrBlank()) {
            daily_test_file_title.text = getString(R.string.daily_test_file_empty)
        } else {
            daily_test_file_title.text = tesHarian.file
        }
    }

    private fun onInitTesFailed(throwable: Throwable) {
        hideLoadingView()
        when (throwable) {
            is NetworkError -> showNetworkErrorView()
            is NoInternetError -> showNetworkErrorView()
            is ResultEmptyError -> {
            }
            else -> showUnknownErrorView()
        }
    }

    private fun initListenToUpdateTesHarianEvent() {
        RxNavi
                .observe(this, Event.CREATE)
                .observeOn(Schedulers.io())
                .flatMap { tesHarianPresenter.listenTesHarianUpdateEvent() }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { reloadTesHarian() }
    }

    private fun showLoadingView() {
        hideAllViews()
        loading_view.makeVisible()
    }

    private fun hideLoadingView() {
        loading_view.makeGone()
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

    private fun hideAllViews() {
        content_container.makeGone()
        unknown_error_view.makeGone()
        network_error_view.makeGone()
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
