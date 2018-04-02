package com.wawakaka.jst.login.composer

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.dashboard.composer.DashboardActivity
import com.wawakaka.jst.datasource.server.model.NetworkError
import com.wawakaka.jst.datasource.server.model.NoInternetError
import com.wawakaka.jst.login.model.GoogleSignInAccountWrapper
import com.wawakaka.jst.login.model.User
import com.wawakaka.jst.login.presenter.LoginPresenter
import com.wawakaka.jst.onboarding.composer.OnBoardingActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val RC_SIGN_IN = 9001
    }

    private val loginPresenter: LoginPresenter by lazy {
        JstApplication.component.proviceLoginPresenter()
    }

    init {
        initLayout()
        initLogin()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .map { loginPresenter.isLoggedIn() }
            .doOnNext { startNextActivityIfLoggedIn(it) }
            .filter { !it }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_login)
            }
    }

    private fun startNextActivityIfLoggedIn(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            if (loginPresenter.shouldShowOnBoarding()) {
                launchOnBoardingActivity()
            } else {
                launchDashboardActivity()
            }
        }
    }

    //todo implement login filter for unauthenticated access
    private fun initLogin() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(Schedulers.io())
            .filter { !loginPresenter.isLoggedIn() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { login_button.setEnabledState(true) }
            .flatMap { RxView.clicks(login_button) }
            .doOnNext { showProgressDialog() }
            .observeOn(Schedulers.io())
            .flatMap {
                loginPresenter
                    .googleSignOutObservable(this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function<Throwable, Observable<Boolean>> {
                        LogUtils.error(TAG, "Error in loginPresenter.googleSignOutObservable", it)

                        onLoginError(it)
                        Observable.just(false)
                    })
            }
            .filter { it }
            .map { loginPresenter.getGoogleApiClient(this) }
            .doOnNext { signIn(it) }
            .flatMap { onActivityResultForGoogleSignInObservable() }
            .flatMap {
                loginPresenter
                    .loginInternalObservable(it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function {
                        LogUtils.error(TAG, "Error in loginPresenter.loginInternalObservable", it)
                        onLoginError(it)
                        Observable.just(User.empty)
                    })
            }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                {
                    LogUtils.debug(TAG, "finish login, start next activity")
                    hideProgressDialog()
                    launchNextActivity()
                },
                {
                    LogUtils.error(TAG, "onError in initLogin", it)
                    onLoginError(it)
                },
                { LogUtils.debug(TAG, "onComplete in initLogin") }
            )
    }

    private fun signIn(googleApiClient: GoogleApiClient) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun onActivityResultForGoogleSignInObservable(): Observable<GoogleSignInAccount> {
        return RxNavi
            .observe(naviComponent, Event.ACTIVITY_RESULT)
            .flatMap {
                if (it.requestCode() == RC_SIGN_IN) {
                    if (it.resultCode() == Activity.RESULT_OK
                        && it.data() != null) {
                        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it.data())
                        if (result.isSuccess) {
                            val googleSignInAccount = result.signInAccount
                            Observable
                                .just(GoogleSignInAccountWrapper(googleSignInAccount))
                                .doOnNext { loginPresenter.firebaseAuthWithGoogle(this, googleSignInAccount) }
                        } else {
                            Observable.error(Throwable())
                        }
                    } else if (it.resultCode() == Activity.RESULT_CANCELED) {
                        Observable.just(GoogleSignInAccountWrapper(null))
                    } else {
                        Observable.error(Throwable())
                    }
                } else {
                    Observable.just(GoogleSignInAccountWrapper(null))
                }
            }
            .doOnNext {
                if (it.googleSignInAccount == null) {
                    hideProgressDialog()
                }
            }
            .filter { it.googleSignInAccount != null }
            .onErrorResumeNext(Function {
                onLoginError(it)
                Observable.just(GoogleSignInAccountWrapper(null))
            })
            .take(1)
            .filter { it.googleSignInAccount != null }
            .map { it.googleSignInAccount!! }
    }

    private fun onLoginError(throwable: Throwable) {
        hideProgressDialog()
        when (throwable) {
            is NetworkError -> showError(getString(R.string.error_network))
            is NoInternetError -> showError(getString(R.string.error_no_internet))
            else -> showError(getString(R.string.error_unknown))
        }
    }

    private fun launchNextActivity() {
        LogUtils.debug(TAG, loginPresenter.shouldShowOnBoarding().toString())
        if (loginPresenter.shouldShowOnBoarding()) {
            launchOnBoardingActivity()
        } else {
            launchDashboardActivity()
        }
    }

    private fun launchOnBoardingActivity() {
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showError(errorMessage: String) {
        ViewUtils.showSnackbarError(findViewById(android.R.id.content), errorMessage)
    }

    private fun showProgressDialog() {
        login_button.enableLoadingState()
    }

    private fun hideProgressDialog() {
        login_button.disableLoadingState()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (p0.errorCode == ConnectionResult.CANCELED) {
            LogUtils.debug(TAG, "onConnectionFailed canceled")
            hideProgressDialog()
            loginPresenter.setGoogleApiClient(null)
        } else {
            LogUtils.debug(TAG, "onConnectionFailed throwable")
            onLoginError(Throwable())
        }
    }
}
