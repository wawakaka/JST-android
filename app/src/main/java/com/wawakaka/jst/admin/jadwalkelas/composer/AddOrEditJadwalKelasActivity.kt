package com.wawakaka.jst.admin.jadwalkelas.composer

import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.bidang.composer.AddOrEditBidangActivity
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
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_jadwal_kelas.*
import org.joda.time.format.DateTimeFormat
import java.util.*

class AddOrEditJadwalKelasActivity : BaseActivity() {

    companion object {
        val TAG = AddOrEditBidangActivity::class.java.simpleName!!
        private const val SIMPLE_DATE_FORMAT_PATTERN = "dd/MM/yyyy"
        private const val DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy"
        private const val LAST_HOUR_OF_DAY = 23
        private const val LAST_MINUTE_OF_HOUR = 59
        private const val LAST_SECOND_OF_MINUTE = 0
        private const val SIMPLE_TIME_FORMAT_PATTERN = "H:mm"
        private const val TIME_FORMAT_PATTERN = "HH:mm"
        private const val TWENTY_FOUR_HOURS_ENABLED = true
    }

    private val jadwalKelas: JadwalKelas? by lazy {
        intent.getSerializableExtra(ExtraUtils.JADWAL_KELAS) as? JadwalKelas
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as? Boolean
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private var progressDialog: DefaultProgressDialog? = null
    private var datePicker: DatePickerDialog? = null
    private var dailyTimePicker: TimePickerDialog? = null
    private val indonesiaLocale = Locale("in")
    private val simpleDateFormatter = DateTimeFormat
            .forPattern(SIMPLE_DATE_FORMAT_PATTERN)
            .withLocale(indonesiaLocale)
    private val dateFormatter = DateTimeFormat
            .forPattern(DATE_FORMAT_PATTERN)
            .withLocale(indonesiaLocale)
    private val simpleTimeFormatter = DateTimeFormat.forPattern(SIMPLE_TIME_FORMAT_PATTERN)
    private val timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT_PATTERN)
    private var hours: Int = 0
    private var minutes: Int = 0
    private val listKelas: MutableList<String> = mutableListOf()

    init {
        initLayout()
        initTanggal()
        initTime()
        initDatePicker()
        initTimePicker()
        initKelasSpinner()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_add_or_edit_jadwal_kelas)
                    initToolbar()
                }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initTanggal() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { setTanggal() }
    }

    private fun setTanggal() {
        tanggal_text.setText(DateUtils.getFormattedDate(jadwalKelas?.tanggal ?: ""))
    }

    private fun initTime() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isEdit == true }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { setTime() }
    }

    private fun setTime() {
        waktu_text.setText(DateUtils.getIso8601Time(jadwalKelas?.tanggal ?: ""))
    }

    private fun initDatePicker() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(tanggal_text) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { showDatePickerDialog() }
    }

    private fun showDatePickerDialog() {
        val now = Calendar.getInstance()

        if (now != null) datePicker = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth -> onDateSelected(year, monthOfYear, dayOfMonth) },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )

        datePicker!!.version = DatePickerDialog.Version.VERSION_2
        datePicker!!.show(fragmentManager, "StartDatePickerDialog")
    }

    private fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        tanggal_text.setText(beautifyDate(year, monthOfYear, dayOfMonth))
    }

    private fun beautifyDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val oldDate = "$dayOfMonth/${calibrateMonthOfYear(monthOfYear)}/$year"
        val oldDateTime = simpleDateFormatter.parseDateTime(oldDate)
        return dateFormatter.print(oldDateTime)
    }

    /**
     * Month from datepicker starts from zero, so we need to add it by one.
     */
    private fun calibrateMonthOfYear(monthOfYear: Int): Int {
        return monthOfYear + 1
    }

    private fun initTimePicker() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(waktu_text) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { showTimePickerDialog() }
    }

    private fun showTimePickerDialog() {
        val now = Calendar.getInstance()

        if (now != null) dailyTimePicker = TimePickerDialog.newInstance(
                { _, hourOfDay, minute, _ ->
                    hours = hourOfDay
                    minutes = minute
                    waktu_text.setText(beautifyTime(hourOfDay, minute))
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
        val oldDateTime = simpleTimeFormatter.parseDateTime(oldTime)
        return timeFormatter.print(oldDateTime)
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
            val kelas = adminPresenter.getKelas().find { it.id == jadwalKelas?.kelaId ?: "" }?.id.toString()
            val position = adapter.getPosition(kelas)
            kelas_spinner.setSelection(position)
        }
    }

    private fun initSaveButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(save_button) }
                .filter { isDateNotEmpty() && isTimeNotEmpty() && isKelasNotEmpty() }
                .map { getDateTime(tanggal_text.text.toString()) }
                .doOnNext { showProgressDialog() }
                .observeOn(Schedulers.io())
                .flatMap {
                    if (isEdit == true) {
                        adminPresenter.updateJadwalKelasObservable(compseJadwalKelas(it))
                    } else {
                        adminPresenter.addJadwalKelasObservable(compseJadwalKelas(it))
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

    private fun getDateTime(date: String): String {
        return DateUtils.getIso8601String(
                dateFormatter.parseDateTime(date)
                        .withHourOfDay(hours)
                        .withMinuteOfHour(minutes)
                        .withSecondOfMinute(LAST_SECOND_OF_MINUTE)
        )
    }

    private fun compseJadwalKelas(date: String): JadwalKelas {
        return JadwalKelas(
                jadwalKelas?.id,
                date,
                kelas_spinner.selectedItem.toString().toInt(),
                jadwalKelas?.listSiswa
        )
    }

    private fun isDateNotEmpty(): Boolean {
        return if (tanggal_text.text.toString().isBlank()) {
            tanggal_error.makeVisible()
            false
        } else {
            tanggal_error.makeGone()
            true
        }
    }

    private fun isTimeNotEmpty(): Boolean {
        return if (waktu_text.text.toString().isBlank()) {
            waktu_error.makeVisible()
            false
        } else {
            waktu_error.makeGone()
            true
        }
    }

    private fun isKelasNotEmpty(): Boolean {
        return if (kelas_spinner.selectedItem.toString().isBlank()) {
            kelas_error.makeVisible()
            false
        } else {
            kelas_error.makeGone()
            true
        }
    }

    private fun onSaveBidangSucceed() {
        adminPresenter.publishRefreshListJadwalKelasEvent()
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