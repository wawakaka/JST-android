package com.wawakaka.jst.journal.composer

import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.bidang.composer.AddOrEditBidangActivity
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.DefaultTextInputEditText
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.journal.model.Kegiatan
import com.wawakaka.jst.journal.model.KegiatanRequestWrapper
import com.wawakaka.jst.journal.presenter.JournalPresenter
import com.wawakaka.jst.kelas.composer.KelasActivity
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_journal.*
import org.joda.time.format.DateTimeFormat
import java.util.*

class AddOrEditJournalActivity : BaseActivity() {

    companion object {
        val TAG = AddOrEditBidangActivity::class.java.simpleName!!
        private const val SIMPLE_TIME_FORMAT_PATTERN = "H:mm"
        private const val TIME_FORMAT_PATTERN = "HH:mm"
        private const val TWENTY_FOUR_HOURS_ENABLED = true
    }

    private val journalPresenter: JournalPresenter by lazy {
        JstApplication.component.provideJournalPresenter()
    }

    private val kegiatan: Kegiatan? by lazy {
        intent.getSerializableExtra(ExtraUtils.KEGIATAN) as? Kegiatan
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as? Boolean
    }

    private val jumlahSesi: Int? by lazy {
        intent.getSerializableExtra(ExtraUtils.SESI) as? Int
    }

    private val idJadwalKelas: Int? by lazy {
        intent.getSerializableExtra(KelasActivity.EXTRA_ID_JADWAL) as? Int
    }

    private var progressDialog: DefaultProgressDialog? = null
    private val simpleTimeFormatter = DateTimeFormat.forPattern(SIMPLE_TIME_FORMAT_PATTERN)
    private val timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT_PATTERN)
    private var hours: Int = 0
    private var minutes: Int = 0
    private var dailyTimePicker: TimePickerDialog? = null

    init {
        initLayout()
        initEdit()
        initSesiStartTimePicker()
        initSesiEndTimePicker()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_add_or_edit_journal)
                initToolbar()
                setSesiMulai()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initEdit() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { isEdit == true }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setSesiMulai()
                setSesiSelesai()
                setMateri()
                setKeterangan()
            }
    }

    private fun setSesiMulai() {
        sesi_start.setText(kegiatan?.sesiMulai ?: "")
    }

    private fun setSesiSelesai() {
        sesi_end.setText(kegiatan?.sesiSelesai ?: "")
    }

    private fun setMateri() {
        materi.setText(kegiatan?.materi)
    }

    private fun setKeterangan() {
        keterangan.setText(kegiatan?.keterangan)
    }

    private fun initSesiStartTimePicker() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(sesi_start) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { showTimePickerDialog(sesi_start) }
    }

    private fun initSesiEndTimePicker() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(sesi_end) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { showTimePickerDialog(sesi_end) }
    }

    private fun showTimePickerDialog(view: DefaultTextInputEditText) {
        val now = Calendar.getInstance()

        if (now != null) dailyTimePicker = TimePickerDialog.newInstance(
            { _, hourOfDay, minute, _ ->
                hours = hourOfDay
                minutes = minute
                view.setText(beautifyTime(hourOfDay, minute))
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            now.get(Calendar.SECOND),
            TWENTY_FOUR_HOURS_ENABLED
        )

        dailyTimePicker!!.version = TimePickerDialog.Version.VERSION_2
        dailyTimePicker!!.show(fragmentManager, "DailyTimePickerDialog")
    }

    private fun beautifyTime(hours: Int, minutes: Int): String {
        val oldTime = "$hours:$minutes"
        val oldDateTime = simpleTimeFormatter.parseLocalTime(oldTime)
        return timeFormatter.print(oldDateTime)
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_button) }
            .filter { isValidMateri() }
            .doOnNext { showProgressDialog() }
            .observeOn(Schedulers.io())
            .flatMap {
                if (isEdit == true) {
                    journalPresenter.updateJournal(kegiatan?.jadwalKelaId
                        ?: 0, KegiatanRequestWrapper(composeKegiatan()))
                } else {
                    journalPresenter.createJournal(KegiatanRequestWrapper(composeKegiatan()))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
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

    private fun isValidMateri(): Boolean {
        return if (materi.text.toString().isNotBlank()) {
            materi_container.isErrorEnabled = false
            materi_container.error = null
            true
        } else {
            materi_container.isErrorEnabled = true
            materi_container.error = getString(R.string.add_or_edit_materi_error)
            false
        }
    }

    private fun composeKegiatan(): Kegiatan {
        return Kegiatan(
            kegiatan?.id,
            sesi_start.text.toString(),
            sesi_end.text.toString(),
            materi.text.toString(),
            keterangan.text.toString(),
            idJadwalKelas
        )
    }

    private fun onSaveBidangSucceed() {
        journalPresenter.publishJournalRefreshListEvent()
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

