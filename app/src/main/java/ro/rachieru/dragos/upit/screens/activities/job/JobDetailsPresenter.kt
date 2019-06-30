package ro.rachieru.dragos.upit.screens.activities.job

import android.Manifest.permission.*
import android.content.Context
import androidx.core.content.ContextCompat
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachieru.dragos.upit.R
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.CallRequest

/**
 * Upit
 *
 * @author Dragos
 * @since 17.06.2019
 */
class JobDetailsPresenter(
    val api: UpitApi,
    val viewDelegate: JobDetailsActivity
) : Presenter() {

    fun getDetails(context: Context, jobId: Int) {
        doIfHasInternet(
            context,
            api.getJobDetails(jobId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onJobDetails(it)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }),
            viewDelegate::showProgress,
            viewDelegate::onNoInternetConnection
        )
    }

    fun checkVideoCallPermissions(context: Context) {
        viewDelegate.showProgressVideoCall()
        add(
            Single
                .create(SingleOnSubscribe<Boolean> { emitter ->
                    val hasPermission = ContextCompat.checkSelfPermission(context, CAMERA) == 0 &&
                            ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == 0 &&
                            ContextCompat.checkSelfPermission(context, MODIFY_AUDIO_SETTINGS) == 0
                    emitter.onSuccess(hasPermission)
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ hasPermission ->
                    viewDelegate.hideProgressVideoCall()
                    if (hasPermission!!) {
                        viewDelegate.onVideoCallPermissionAvailable()
                    } else {
                        viewDelegate.onVideoCallRequestPermissions()
                    }
                }, { throwable ->
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onError(throwable)
                })
        )
    }

    fun startVideoCall(context: Context, calledId: String) {
        doIfHasInternet(
            context,
            api.callUser(CallRequest(calledId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgressVideoCall()
                    if (it.isSuccess) {
                        viewDelegate.onVideoCallCanStart(it)
                    } else when (it.message) {
                        "no_devices" -> {
                            viewDelegate.onVideoCallError(context.getString(R.string.error_user_no_devices))
                        }
                        "not_found" -> {
                            viewDelegate.onVideoCallError(context.getString(R.string.error_user_not_found))
                        }
                        else -> viewDelegate.onError(Throwable(it.message))
                    }
                }, { throwable ->
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onError(throwable)
                }),
            onStart = viewDelegate::showProgressVideoCall,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }


    fun cancelVideoCall(context: Context, callerId: Int, calledId: String) {
        doIfHasInternet(
            context,
            d = api.cancelCall(CallRequest(calledId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgressVideoCall()
                    if (it.isSuccess) {
                        viewDelegate.onVideoCallRejected()
                    } else {
                        viewDelegate.onVideoCallError(it.message)
                    }
                }, { throwable ->
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onError(throwable)
                }),
            onStart = viewDelegate::showProgressVideoCall,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}