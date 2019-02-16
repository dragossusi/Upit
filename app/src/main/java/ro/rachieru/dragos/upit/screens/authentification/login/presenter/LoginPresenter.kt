package ro.rachieru.dragos.upit.screens.authentification.login.presenter

import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ro.rachierudragos.upitapi.UpitApi
import ro.rachieru.dragos.upit.base.IBaseViewDelegate
import ro.rachieru.dragos.upit.base.Presenter
import ro.rachieru.dragos.upit.screens.authentification.login.view.ILoginViewDelegate
import ro.rachierudragos.upitapi.UserDetails

class LoginPresenter(
    val api: UpitApi,
    val viewDelegate: ILoginViewDelegate
) : Presenter(), ILoginPresenter {

    override fun login(username: String, password: String) {
        api.login(username, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(t: Boolean) {
                    viewDelegate.onLoggedIn(
                        UserDetails(
                            1,
                            "Dragos Rachieru",
                            "dragossusi",
                            "rachierudragos97@gmail.com",
                            "0736593100"
                        )
                    )
                }

                override fun onSubscribe(d: Disposable) {
                    addDisposable(d)
                }

                override fun onError(e: Throwable) {
                    viewDelegate.onError(e)
                }
            })
    }
}