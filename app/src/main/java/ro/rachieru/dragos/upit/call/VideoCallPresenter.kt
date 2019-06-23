package ro.rachieru.dragos.upit.call

import android.Manifest.permission.*
import android.content.Context
import androidx.core.content.ContextCompat
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi


/**
 * Upit
 *
 * @author Dragos
 * @since 13.06.2019
 */
class VideoCallPresenter(
    val api: UpitApi,
    val viewDelegate: CalledByUserActivity
) : Presenter() {

    fun checkVideoCallPermissions(context: Context) {
        viewDelegate.showProgressVideoCall()
        add(
            Completable.fromAction {
                val hasPermission = ContextCompat.checkSelfPermission(context, CAMERA) == 0 &&
                        ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == 0 &&
                        ContextCompat.checkSelfPermission(context, MODIFY_AUDIO_SETTINGS) == 0
                if (!hasPermission)
                    throw CallPermissionException()
            }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onVideoCallPermissionAvailable()
                }, {
                    viewDelegate.hideProgressVideoCall()
                    if (it is CallPermissionException)
                        viewDelegate.onVideoCallRequestPermissions()
                    else viewDelegate.onVideoCallError(it.message!!)
                })
        )
    }

    fun startVideoCall(context: Context, callerId: Int, calledId: String) {
        doIfHasInternet(
            context,
            api.callUser(calledId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onVideoCallCanStart(it)
                }, {
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onError(it.message!!)
                }), viewDelegate::showProgressVideoCall, viewDelegate::onNoInternetConnection
        )
    }

    fun cancelVideoCall(context: Context, callerId: String, calledId: String) {
        doIfHasInternet(
            context,
            api.cancelCall(callerId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onVideoCallRejected()
                }, {
                    viewDelegate.hideProgressVideoCall()
                    viewDelegate.onError(it.message!!)
                }),
            viewDelegate::showProgressVideoCall,
            viewDelegate::hideProgressVideoCall
        )
    }
}
