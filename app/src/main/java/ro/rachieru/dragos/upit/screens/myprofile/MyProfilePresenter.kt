package ro.rachieru.dragos.upit.screens.myprofile

import android.content.Context
import android.net.Uri
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.request.UserDetailsRequest
import java.io.File


/**
 * Upit
 *
 * @author Dragos
 * @since 09.06.2019
 */
class MyProfilePresenter(
    val api: UpitApi,
    val viewDelegate: MyProfileActivity
) : Presenter() {

    fun getDetails(context: Context) {
        doIfHasInternet(
            context,
            api.getMyUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onUserDetails(it)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

    fun changeAvatar(context: Context, resultUri: Uri) {
        doIfHasInternet(
            context,
            Single.fromCallable {
                val file = File(resultUri.getPath())
                val fbody = RequestBody.create(MediaType.parse("image/*"), file)
                MultipartBody.Part.createFormData("file", file.name, fbody)
            }
                .subscribeOn(Schedulers.io())
                .flatMap(api::changeAvatar)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onImageChanged(it)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

    fun changeCV(context: Context, resultUri: Uri) {
        doIfHasInternet(
            context,
            Single.fromCallable {
                val file = File(resultUri.getPath())
                val fbody = RequestBody.create(MediaType.parse("application/pdf"), file)
                MultipartBody.Part.createFormData("file", file.name, fbody)
            }
                .subscribeOn(Schedulers.io())
                .flatMap(api::uploadCV)
                .map {
                    it to it.substring(it.lastIndexOf('/') + 1)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onCvChanged(it)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

    fun saveProfile(
        context: Context,
        firstName: String,
        lastName: String
    ) {
        doIfHasInternet(
            context,
            api.saveUserDetails(UserDetailsRequest(firstName, lastName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                }, {
                    viewDelegate.hideProgress()
                }),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}