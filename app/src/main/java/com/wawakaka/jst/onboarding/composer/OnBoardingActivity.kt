package com.wawakaka.jst.onboarding.composer

import android.content.Intent
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.DefaultProgressDialog
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.dashboard.composer.DashboardActivity
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.onboarding.presenter.OnBoardingPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnBoardingActivity : BaseActivity() {

    companion object {
        private val TAG = OnBoardingActivity::class.java.simpleName
    }

    private val onBoardingPresenter: OnBoardingPresenter by lazy {
        JstApplication.component.provideOnBoardingPresenter()
    }
    //todo add all string to resource
    private val progressDialog: DefaultProgressDialog by lazy {
        DefaultProgressDialog(this, "memperoses", false)
    }

    init {
        initLayout()
        initSaveButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_onboarding)
            }
    }

    private fun initSaveButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(save) }
            .filter { isValidName() }
            .doOnNext { progressDialog.show() }
            .map { setUser() }
            .observeOn(Schedulers.io())
            .flatMap {
                onBoardingPresenter
                    .updateUserObservable(it)
            }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "success update username, start next activity")
                    progressDialog.dismiss()
                    launchDashboardActivity()
                },
                {
                    LogUtils.error(TAG, "onError in initSaveButton", it)
                    onSaveFailed(it)
                },
                { LogUtils.debug(TAG, "onComplete in initSaveButton") }
            )
    }

    private fun isValidName(): Boolean {
        //todo update this method to display error message
        return getName().isNotBlank()
    }

    private fun setUser(): User {
        return User(onBoardingPresenter.getUser().email, getName(), null, null, null, null)
    }

    private fun getName(): String {
        return name.text.toString()
    }

    private fun launchDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSaveFailed(throwable: Throwable) {
        progressDialog.dismiss()
        //todo add all strings to resource
        when (throwable) {
            is NetworkError -> showError("error network")
            is NoInternetError -> showError("no internet error")
            else -> showError("unknown error")
        }
    }

    private fun showError(errorMessage: String) {
        ViewUtils.showSnackbarError(findViewById(android.R.id.content), errorMessage)
    }
}
