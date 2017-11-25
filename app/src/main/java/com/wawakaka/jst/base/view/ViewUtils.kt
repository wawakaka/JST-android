package com.wawakaka.jst.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.androidadvance.topsnackbar.TSnackbar
import com.wawakaka.jst.R
import io.reactivex.Observable

/**
 * Created by wawakaka on 10/11/2017.
 */
object ViewUtils {

    private val TAG = ViewUtils::class.java.simpleName

    private const val SNACKBAR_MAX_LINES = 5

    @SuppressLint("RestrictedApi")
        /**
     * Get layout inflater using context that is wrapped by app theme style. Always use this layout
     * inflater to avoid crash in some devices. Return null if context passed is null.
     */
    fun getLayoutInflater(context: Context?): LayoutInflater? {
        if (context == null) {
            return null
        }

        val contextWrapped = ContextThemeWrapper(context, R.style.AppTheme)
        return LayoutInflater.from(contextWrapped)
    }

    /**
     * Show snackbar error with error text provided inside container
     *
     * @param container View container to show snackbar
     * @param error Error message to be shown
     */
    fun showSnackbarError(container: View, error: String) {
        val snackBar = TSnackbar.make(container, error, Snackbar.LENGTH_SHORT)

        // Get snackbar view
        val view = snackBar.view

        // Set snackbar view background
        view.setBackgroundColor(ContextCompat.getColor(container.context, R.color.red))

        // Set snackbar text properties
        val snackBarText = view.findViewById<TextView>(android.support.design.R.id.snackbar_text)

        snackBarText?.setTextColor(Color.WHITE)
        snackBarText?.gravity = Gravity.CENTER_HORIZONTAL
        snackBarText?.maxLines = SNACKBAR_MAX_LINES

        // Show snackbar
        snackBar.show()
    }

    /**
     * Show confirmation dialog to the user as an observable. Will emit true if confirmed,
     * false otherwise.
     */
    //todo string
    fun showConfirmationObservable(context: Context,
                                   confirmationTitle: String,
                                   confirmationMessage: String): Observable<Boolean> {
        return Observable.create { subscriber ->
//            val spanTitle = FontUtils.getBoldFontSpannableTitle(context, confirmationTitle)
//            val spanMessage = FontUtils.getDefaultFontSpannableMessage(context, confirmationMessage)
            val confirmationDialog = AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(confirmationTitle)
                .setMessage(confirmationMessage)
                .setPositiveButton("yes") { dialog, _ ->
                    subscriber.onNext(true)
                    subscriber.onComplete()
                    dialog.dismiss()
                }
                .setNegativeButton("no") { dialog, _ ->
                    subscriber.onNext(false)
                    subscriber.onComplete()
                    dialog.dismiss()
                }
                .setOnCancelListener {
                    subscriber.onNext(false)
                    subscriber.onComplete()
                }
                .create()

            confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            confirmationDialog.show()
        }
    }

    fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
    }

    fun showToastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
    }

    fun showToastLong(context: Context, message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
    }

    fun showToastShort(context: Context, message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
    }

}