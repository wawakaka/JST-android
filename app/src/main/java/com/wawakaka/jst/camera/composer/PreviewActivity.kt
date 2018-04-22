package com.wawakaka.jst.camera.composer

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.camera.presenter.CameraPresenter
import com.wawakaka.jst.camera.view.ResultHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_preview.*


class PreviewActivity : BaseActivity() {

    companion object {
        private val TAG = PreviewActivity::class.java.simpleName
    }

    private val cameraPresenter: CameraPresenter by lazy {
        JstApplication.component.provideCameraPresenter()
    }

    init {
        initLayout()
        initImagePreview()
        initUploadButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_preview)
            }
    }

    private fun initImagePreview() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .map { ResultHolder.image }
            .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe({
                image.setImageBitmap(it)
                image.makeVisible()
                actualResolution!!.text = it.width.toString() + " x " + it.height
                approxUncompressedSize!!.text = getApproximateFileMegabytes(it).toString() + "MB"
                captureLatency!!.text = "" + ResultHolder.timeToCallback + " milliseconds"
            },
                {
                    LogUtils.error(TAG, "error in initImagePreview", it)
                    finish()
                }
            )
    }

    private fun initUploadButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(upload) }
            .map { ResultHolder.image }
            .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe({
                uploadImage(it)
            },
                {
                    LogUtils.error(TAG, "error in initUploadButton", it)
                    finish()
                }
            )
    }

    private fun uploadImage(bitmap: Bitmap) {
        MediaManager.get()
            .upload(getRealPathFromUri(saveImage(bitmap)))
            .unsigned("jst_android")
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    LogUtils.debug("cdn onSuccess", requestId ?: "")
                    LogUtils.debug("cdn onSuccess", resultData.toString())
                    LogUtils.debug("cdn onSuccess", resultData!!["secure_url"].toString())
//                    launchCreateReportActivity(resultData!!["secure_url"].toString())
                    ViewUtils.showSnackbarError(root_container, "sukses")
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
                }

                override fun onStart(requestId: String?) {
                    LogUtils.debug("cdn onStart", requestId.toString())
                }

            })
            .dispatch()
    }

    private fun getApproximateFileMegabytes(bitmap: Bitmap): Float {
        return (bitmap.rowBytes * bitmap.height / 1024 / 1024).toFloat()
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

}
