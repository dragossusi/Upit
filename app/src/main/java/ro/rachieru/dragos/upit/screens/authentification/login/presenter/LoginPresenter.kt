package ro.rachieru.dragos.upit.screens.authentification.login.presenter

import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ro.rachieru.dragos.upit.api.UpitApi
import ro.rachieru.dragos.upit.base.IBaseViewDelegate
import ro.rachieru.dragos.upit.base.Presenter

class LoginPresenter(
    val api: UpitApi,
    val viewDelegate: IBaseViewDelegate
) : Presenter(), ILoginPresenter {

    override fun login(username: String, password: String) {
        api.login(username, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(t: Boolean) {

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