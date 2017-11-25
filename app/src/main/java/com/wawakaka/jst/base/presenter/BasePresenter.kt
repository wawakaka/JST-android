package com.wawakaka.jst.base.presenter

import com.birbit.android.jobqueue.CancelResult
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.TagConstraint
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.model.ResultEmptyError
import io.reactivex.Observable

/**
 * Created by wawakaka on 7/25/2017.
 */
open class BasePresenter {

    companion object {
        private val TAG = BasePresenter::class.java.simpleName
    }

    /**
     * Handle if observable emits an empty result
     *
     * @param observable Observable source to be checked
     * @param resultEmptyCheckingFunc Function to check whether the result is empty or not
     */
    inline fun <T> handleEmptyResult(observable: Observable<T>,
                                     crossinline resultEmptyCheckingFunc: (obj: T?) -> Boolean): Observable<T> {
        return observable
            .flatMap {
                if (resultEmptyCheckingFunc(it)) {
                    Observable.error<T>(ResultEmptyError(Throwable()))
                } else {
                    Observable.just(it)
                }
            }
    }

    fun clearSaveData() {
        takeLocalRequestManager()?.let {
            it.saveUser(null)
            it.saveKelas(mutableListOf())
        }
    }


    /**
     * Cancel all ongoing jobs. Call it after logged out.
     */
    fun cancelAllOngoingJobs() {
        takeJobManager()?.let {
            it.cancelJobsInBackground(
                CancelResult.AsyncCancelCallback { LogUtils.debug(TAG, "All jobs might be cancelled") },
                TagConstraint.ANY
            )
        }
    }

    /**
     * Override this for those who needs
     */
    open fun takeLocalRequestManager(): LocalRequestManager? = null


    /**
     * Override this for those who needs
     */
    open fun takeJobManager(): JobManager? = null
}