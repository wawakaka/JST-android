package com.wawakaka.jst.pengeluaran.composer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.camera.composer.CameraActivity
import com.wawakaka.jst.camera.view.ResultHolder
import com.wawakaka.jst.datasource.model.ResultEmptyError
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.pengeluaran.model.Pengeluaran
import com.wawakaka.jst.pengeluaran.model.PengeluaranRequestWrapper
import com.wawakaka.jst.pengeluaran.presenter.PengeluaranPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_or_edit_pengeluaran.*

class AddOrEditPengeluaranActivity : BaseActivity() {

    companion object {
        private val TAG = AddOrEditPengeluaranActivity::class.java.simpleName
        private const val EDIT_REQUEST = 2041
        private const val MY_WRITE_EXTERNAL_STORAGE = 1234
        private const val UPLOAD_PRESET = "jst_android"
    }

    private val pengeluaranPresenter: PengeluaranPresenter by lazy {
        JstApplication.component.providePengeluaranPresenter()
    }

    private val isEdit: Boolean by lazy {
        intent.getSerializableExtra(ExtraUtils.IS_EDIT) as Boolean
    }

    private val pengeluaran: Pengeluaran by lazy {
        intent.getSerializableExtra(ExtraUtils.PENGELUARAN) as Pengeluaran
    }

    private var progressDialog: DefaultProgressDialog? = null
    private var imageSecureUrl: String = ""

    init {
        initLayout()
        initEditData()
        initImagePreview()
        initNetworkErrorView()
        initUnknownErrorView()
        initTakePictureButton()
        initSaveButton()
        initUploadImageSuccessEvent()
        initUploadImageFailedEvent()
        initOnResume()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_add_or_edit_pengeluaran)
                initToolbar()
                initRequestPermission()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun initEditData() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { isEdit }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                id_text.setText(pengeluaran.id ?: 0)
                id_text.makeVisible()
                tanggal_text.setText(DateUtils.getFormattedDate(pengeluaran.tanggal ?: ""))
                tanggal_text.isEnabled = false
                barang_text.setText(pengeluaran.barang)
                biaya_text.setText(pengeluaran.biaya ?: 0)
                keterangan_text.setText(pengeluaran.keterangan ?: "")
                tanggal_container.makeVisible()
            }
    }

    private fun initImagePreview() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .map { ResultHolder.image ?: ByteArray(0) }
            .filter { it.isNotEmpty() }
            .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe({
                gambar.setImageBitmap(it)
                gambar.makeVisible()
            },
                {
                    LogUtils.error(TAG, "error in initImagePreview", it)
                    finish()
                }
            )
    }

    private fun initNetworkErrorView() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                network_error_view.setActionOnClickListener(View.OnClickListener {
                    network_error_view.isEnabled = false
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
                })
            }
    }

    private fun initTakePictureButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(take_picture_button) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchCameraActivity()
            }
    }

    private fun launchCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, EDIT_REQUEST)
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save_button) }
            .filter { showErrorIfNotValid() }
            .doOnNext { showProgressDialog() }
            .map { ResultHolder.image ?: ByteArray(0) }
            .filter { continueSaveToServerIfNoImageTaken(it) }
            .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .subscribeOn(Schedulers.io())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe { uploadImage(it) }
    }

    private fun showErrorIfNotValid() =
        when {
            nameNotEmpty() && priceNotEmpty() -> {
                hidePriceError()
                hideNameError()
                true
            }
            nameEmpty() -> {
                showNameError()
                false
            }
            priceEmpty() -> {
                showPriceError()
                false
            }
            else -> {
                showNameError()
                showPriceError()
                false
            }
        }

    private fun nameNotEmpty() = barang_text.text.isNotBlank()

    private fun priceNotEmpty() = biaya_text.text.isNotBlank()

    private fun nameEmpty() = barang_text.text.isNullOrBlank()

    private fun priceEmpty() = biaya_text.text.isNullOrBlank()

    private fun showNameError() {
        barang_container.error = getString(R.string.add_or_edit_nama_barang_error)
        barang_container.isErrorEnabled = true
    }

    private fun hideNameError() {
        barang_container.error = null
        barang_container.isErrorEnabled = false
    }

    private fun showPriceError() {
        biaya_container.error = getString(R.string.add_or_edit_bidang_error)
        biaya_container.isErrorEnabled = true
    }

    private fun hidePriceError() {
        biaya_container.error = null
        biaya_container.isErrorEnabled = false
    }

    private fun continueSaveToServerIfNoImageTaken(byteArray: ByteArray) =
        if (byteArray.isNotEmpty()) {
            true
        } else {
            saveToServer()
            false
        }

    private fun uploadImage(bitmap: Bitmap) {
        MediaManager.get()
            .upload(
                getRealPathFromUri(
                    saveImage(bitmap)
                )
            )
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    LogUtils.debug("cdn onSuccess", requestId ?: "")
                    LogUtils.debug("cdn onSuccess", resultData.toString())
                    LogUtils.debug("cdn onSuccess", resultData!!["secure_url"].toString())
                    imageSecureUrl = resultData["secure_url"].toString()
                    pengeluaranPresenter.publishUploadImageSuccessEvent()
                    ViewUtils.showSnackbarError(root_container, "Success")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    LogUtils.debug("cdn onProgress", requestId ?: "")
                    LogUtils.debug("cdn onProgress", bytes.toString())
                    LogUtils.debug("cdn onProgress", totalBytes.toString())
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    LogUtils.debug("cdn onReschedule", requestId.toString())
                    LogUtils.debug("cdn onReschedule", error?.code.toString())
                    LogUtils.debug("cdn onReschedule", error?.description.toString())
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    LogUtils.debug("cdn onError", requestId.toString())
                    LogUtils.debug("cdn onError", error.toString())
                    LogUtils.debug("cdn onError", error?.code.toString())
                    LogUtils.debug("cdn onError", error?.description.toString())
                    pengeluaranPresenter.publishUploadImageFailedEvent()
                }

                override fun onStart(requestId: String?) {
                    LogUtils.debug("cdn onStart", requestId.toString())
                }
            })
            .dispatch()
    }

    private fun saveImage(bitmap: Bitmap): Uri {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "",
            ""
        )
        // Parse the gallery image url to uri
        return Uri.parse(savedImageURL)
    }

    private fun getRealPathFromUri(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun initUploadImageSuccessEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { pengeluaranPresenter.listenUploadImageSuccessEvent() }
            .subscribeOn(Schedulers.io())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                saveToServer()
            }
    }

    private fun saveToServer() {
        pengeluaranPresenter.createPengeluaranObservable(buildPengeluaran())
            .filter { it }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    onSavePengeluaranSucceed()
                },
                {
                    LogUtils.error(TAG, "error in initSaveButton", it)
                    onSavePengeluaranError(it)
                }
            )
    }

    private fun initUploadImageFailedEvent() {
        RxNavi
            .observe(this, Event.CREATE)
            .observeOn(Schedulers.io())
            .flatMap { pengeluaranPresenter.listenUploadImageFailedEvent() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                hideProgressDialog()
                showSnackbarError(getString(R.string.add_or_edit_Image_upload_failed))
            }
    }

    private fun buildPengeluaran(): PengeluaranRequestWrapper {
        return PengeluaranRequestWrapper(
            Pengeluaran(
                null,
                null,
                DateUtils.getCurrentDateInIso8601String(),
                pengeluaranPresenter.getUser().email,
                barang_text.text.toString(),
                biaya_text.text.toString().toInt(),
                keterangan_text.text.toString(),
                imageSecureUrl
            )
        )
    }

    private fun onSavePengeluaranSucceed() {
        hideProgressDialog()
        pengeluaranPresenter.publishRefreshListPengeluaranEvent()
        finish()
    }

    private fun onSavePengeluaranError(throwable: Throwable) {
        hideProgressDialog()

        when (throwable) {
            is NetworkError -> {
                showSnackbarError(getString(R.string.error_network))
            }
            is NoInternetError -> {
                showSnackbarError(getString(R.string.error_no_internet))

            }
            is ResultEmptyError -> {
                //todo add empty screen or leave it blank
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

    private fun initOnResume() {
        RxNavi
            .observe(naviComponent, Event.RESUME)
            .observeOn(AndroidSchedulers.mainThread())
            .map { ResultHolder.image ?: ByteArray(0) }
            .filter { it.isNotEmpty() }
            .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe({
                gambar.setImageBitmap(it)
                gambar.makeVisible()
            },
                {
                    LogUtils.error(TAG, "error in initImagePreview", it)
                    finish()
                }
            )
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
