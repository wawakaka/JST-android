package com.wawakaka.jst.admin.event.composer

import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.kelas.composer.AddOrEditKelasActivity
import com.wawakaka.jst.admin.presenter.AdminPresenter
import com.wawakaka.jst.admin.sekolah.model.Sekolah
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.*
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.event.model.EventRequestWrapper
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_event.*
import org.joda.time.format.DateTimeFormat
import java.util.*

class AddOrEditEventActivity : BaseActivity() {

    companion object {
        val TAG = AddOrEditEventActivity::class.java.simpleName!!
        private const val SIMPLE_DATE_FORMAT_PATTERN = "dd/MM/yyyy"
        private const val DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy"
        private const val LAST_SECOND_OF_MINUTE = 0
    }

    private val adminPresenter: AdminPresenter by lazy {
        JstApplication.component.provideAdminPresenter()
    }

    private val event: com.wawakaka.jst.event.model.Event? by lazy {
        intent.getSerializableExtra(ExtraUtils.EVENT) as? com.wawakaka.jst.event.model.Event
    }

    private val isEdit: Boolean? by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as? Boolean
    }

    private var datePicker: DatePickerDialog? = null
    private val indonesiaLocale = Locale("in")
    private val simpleDateFormatter = DateTimeFormat
        .forPattern(SIMPLE_DATE_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)
    private val dateFormatter = DateTimeFormat
        .forPattern(DATE_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)
    private var progressDialog: DefaultProgressDialog? = null
    private val listSekolah: MutableList<String> = mutableListOf()

    init {
        initLayout()
        initTanggal()
        initTanggalMulaiPicker()
        initTanggalSelesaiPicker()
        initSekolahSpinner()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_add_or_edit_event)
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
        tanggal_mulai_text.setText(DateUtils.getFormattedDate(event?.tanggalMulai ?: ""))
        tanggal_selesai_text.setText(DateUtils.getFormattedDate(event?.tanggalSelesai ?: ""))
    }

    private fun initTanggalMulaiPicker() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(tanggal_mulai_text) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { showDatePickerDialog(tanggal_mulai_text) }
    }

    private fun initTanggalSelesaiPicker() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(tanggal_selesai_text) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { showDatePickerDialog(tanggal_selesai_text) }
    }

    private fun showDatePickerDialog(view: DefaultEditText) {
        val now = Calendar.getInstance()

        if (now != null) datePicker = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth -> onDateSelected(view, year, monthOfYear, dayOfMonth) },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        datePicker!!.version = DatePickerDialog.Version.VERSION_2
        datePicker!!.show(fragmentManager, "StartDatePickerDialog")
    }

    private fun onDateSelected(view: DefaultEditText, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        view.setText(beautifyDate(year, monthOfYear, dayOfMonth))
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

    private fun initSekolahSpinner() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap {
                adminPresenter
                    .loadAllSekolah()
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(AddOrEditKelasActivity.TAG, "Error in adminPresenter.loadAllSekolah", it)

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
            val sekolah = adminPresenter.getSekolah().find { it.nama == event?.sekolahNama ?: "" }?.nama
            val position = adapter.getPosition(sekolah)
            sekolah_spinner.setSelection(position)
        }
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_button) }
            .filter { isValidEvent() }
            .doOnNext {
                showProgressDialog()
                LogUtils.debug("isedut", isEdit.toString())
            }
            .observeOn(Schedulers.io())
            .flatMap {
                if (isEdit == true) {
                    adminPresenter.updateEventObservable(event?.id ?: 0,
                        EventRequestWrapper(composeEvent()))
                } else {
                    adminPresenter.createEventObservable(EventRequestWrapper(composeEvent()))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    onSaveEventSucceed()
                },
                {
                    LogUtils.error(TAG, "error in initSaveButton", it)
                    onSaveEventError(it)
                }
            )
    }

    private fun isValidEvent(): Boolean {
        return isSekolahNotEmpty() && isTanggalMulaiNotEmpty() && isTanggalSelesaiNotEmpty()
    }

    private fun isSekolahNotEmpty(): Boolean {
        return if (sekolah_spinner.selectedItem.toString().isNullOrBlank()) {
            sekolah_error.makeVisible()
            false
        } else {
            sekolah_error.makeGone()
            true
        }
    }

    private fun isTanggalMulaiNotEmpty(): Boolean {
        return if (tanggal_mulai_text.text.toString().isBlank()) {
            tanggal_mulai_error.makeVisible()
            false
        } else {
            tanggal_mulai_error.makeGone()
            true
        }
    }

    private fun isTanggalSelesaiNotEmpty(): Boolean {
        return if (tanggal_selesai_text.text.toString().isBlank()) {
            tanggal_selesai_error.makeVisible()
            false
        } else {
            tanggal_selesai_error.makeGone()
            true
        }
    }

    private fun composeEvent(): com.wawakaka.jst.event.model.Event {
        return com.wawakaka.jst.event.model.Event(
            event?.id,
            getDateTime(tanggal_mulai_text.text.toString()),//todo change this
            getDateTime(tanggal_selesai_text.text.toString()),//todo change this
            event?.listKelas,
            event?.listPengeluaran,
            sekolah_spinner.selectedItem.toString()//todo change this
        )
    }

    private fun getDateTime(date: String): String {
        val hours = 0
        val minutes = 0
        return DateUtils.getIso8601String(
            dateFormatter.parseDateTime(date)
                .withHourOfDay(hours)
                .withMinuteOfHour(minutes)
                .withSecondOfMinute(LAST_SECOND_OF_MINUTE)
        )
    }

    private fun onSaveEventSucceed() {
        adminPresenter.publishRefreshListAdminEvent()
        hideProgressDialog()
        finish()
    }

    private fun onSaveEventError(throwable: Throwable) {
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
