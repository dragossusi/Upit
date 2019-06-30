package ro.rachieru.dragos.upit.screens.fragments.login.presenter

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachieru.dragos.upit.screens.fragments.login.view.LoginViewDelegate
import ro.rachierudragos.upitapi.UpitApi

class LoginPresenter(
    val api: UpitApi,
    val viewDelegate: LoginViewDelegate
) : Presenter(), ILoginPresenter {

    override fun login(context: Context, email: String, password: String) {
        doIfHasInternet(context,
            d = Single.fromCallable<String> {
                val it = Tasks.await(FirebaseInstanceId.getInstance().instanceId)
                it.token
            }.subscribeOn(Schedulers.io())
                .flatMap { api.login(email, password, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    if (it.accessToken.isNullOrEmpty())
                        viewDelegate.onError(Throwable())
                    else viewDelegate.onLoginSuccess(it)
                }, viewDelegate::onError),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}
