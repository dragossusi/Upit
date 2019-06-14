package ro.rachieru.dragos.upit.screens.fragments.login.presenter

import com.google.android.gms.tasks.Tasks
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachieru.dragos.upit.screens.fragments.login.view.LoginViewDelegate
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.request.LoginRequest

class LoginPresenter(
    val api: UpitApi,
    val viewDelegate: LoginViewDelegate
) : Presenter(), ILoginPresenter {

    override fun login(email: String, password: String) {
        this += Single.fromCallable<String> {
            val it = Tasks.await(FirebaseInstanceId.getInstance().instanceId)
            it.token
        }.flatMap { api.login(LoginRequest(email, password, it)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(viewDelegate::onLoginSuccess, viewDelegate::onError)
    }

}
