package com.wawakaka.jst.login.presenter


import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wawakaka.jst.BuildConfig
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.base.utils.toResultEmptyErrorIfEmpty
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.login.model.User
import io.reactivex.Observable

/**
 * Created by wawakaka on 10/11/2017.
 */
class LoginPresenter(private val serverRequestManager: ServerRequestManager,
                     private val localRequestManager: LocalRequestManager) {

    companion object {
        private val TAG = LoginPresenter::class.java.simpleName
        private const val ADMIN = "admin"
    }

    private var googleApiClient: GoogleApiClient? = null

    fun googleSignOutObservable(activity: AppCompatActivity): Observable<Boolean> {
        return Observable.create<Boolean> { subscriber ->
            getGoogleApiClient(activity)
            googleApiClient?.let {
                if (!it.isConnected) {
                    it.blockingConnect()
                }

                Auth.GoogleSignInApi
                    .signOut(it)
                    .setResultCallback {
                        if (it.isSuccess) {
                            subscriber.onNext(true)
                        } else {
                            subscriber.onNext(false)
                        }
                        subscriber.onComplete()
                    }

                getFireBaseAuth().signOut()
            }
        }
    }

    fun setGoogleApiClient(googleApiClient: GoogleApiClient?) {
        this.googleApiClient = googleApiClient
    }

    fun getGoogleApiClient(activity: AppCompatActivity): GoogleApiClient {
        if (googleApiClient == null) {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("220324769521-vt2u48qgr59t7bsuv5a0rikq8shs5kal.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .requestId()
                .build()

            googleApiClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, activity as GoogleApiClient.OnConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
        }
        return googleApiClient!!
    }

    fun firebaseAuthWithGoogle(context: Context,
                               googleSignInAccount: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount!!.idToken, null)
        getFireBaseAuth()
            .signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    LogUtils.debug(TAG, "sucess sign-in")
                    val user = getFireBaseAuth().currentUser
                } else {
                    ViewUtils.showToastLong(context, "gagal login")
                }
            }
    }

    fun loginInternalObservable(googleSignInAccount: GoogleSignInAccount): Observable<User> {
        val user = User(
            googleSignInAccount.email,
            null,
            googleSignInAccount.photoUrl.toString(),
            isAdminBuild(),
            null,
            null
        )
        return serverRequestManager
            .loginObservable(user)
            .toResultEmptyErrorIfEmpty { it?.data?.isEmpty() ?: true }
            .map { it.data!! }
            .doOnNext { saveUserLogin(it) }
    }

    private fun isAdminBuild(): Boolean = BuildConfig.FLAVOR == ADMIN

    private fun saveUserLogin(user: User) {
        localRequestManager.saveUser(user)
    }

    private fun getUser(): User = localRequestManager.getUser()

    fun isLoggedIn(): Boolean = localRequestManager.isLoggedIn()

    fun shouldShowOnBoarding(): Boolean = getUser().nama.isNullOrEmpty()

    fun getFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}