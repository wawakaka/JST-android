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

    fun showInfoDialogObservable(context: Context,
                                 message: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            //            val spanMessage = FontUtils.getDefaultFontSpannableMessage(context, message)
//            val spanOk = FontUtils.getDefaultSpannableString(context, context.getString(R.string.ok_button))
            val infoDialog = AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage(message)
                    .setPositiveButton(context.getString(R.string.ok_button)) { dialog, _ ->
                        subscriber.onNext(true)
                        subscriber.onComplete()
                        dialog.dismiss()
                    }
                    .create()

            infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            infoDialog.show()
        }
    }

    /**
     * Show confirmation dialog to the user as an observable. Will emit true if confirmed,
     * false otherwise.
     */
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
                .setPositiveButton(context.getString(R.string.dialog_yes)) { dialog, _ ->
                    subscriber.onNext(true)
                    subscriber.onComplete()
                    dialog.dismiss()
                }
                .setNegativeButton(context.getString(R.string.dialog_no)) { dialog, _ ->
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

    /**
     * Show confirmation dialog to the user about the unsaved data as an observable.
     * Will emit true if confirmed, false otherwise.
     */
    fun showUnsavedConfirmationDialog(context: Context): Observable<Boolean> {
        return showConfirmationObservable(
            context,
            context.getString(R.string.unsaved_warning_title),
            context.getString(R.string.unsaved_warning_message)
        )
    }

    /**
     * Show options dialog to the user as an Observable. Will return empty string if nothing is
     * selected or the dialog is cancelled.
     *
     * @param context   Context used to create dialog
     * @param title     Title of the dialog. Pass null if you don't want to show title
     * @param options   List of options to be shown
     */
    fun showOptionsObservable(context: Context,
                              title: String?,
                              options: List<String>): Observable<String> {
        return Observable.create { subscriber ->
            //            val spanTitle = FontUtils.getBoldFontSpannableTitle(context, title ?: "")
            val optionsDialog = AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(title)
                    .setItems(
                            options
//                        .map { FontUtils.getLightSpannableString(context, it) }
                                    .map { it }
                                    .toTypedArray(),
                            { dialog, which ->
                                subscriber.onNext(options[which])
                                subscriber.onComplete()
                                dialog.dismiss()
                            }
                    )
                    .setOnCancelListener {
                        subscriber.onNext("")
                        subscriber.onComplete()
                    }
                    .create()

            optionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            optionsDialog.show()
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