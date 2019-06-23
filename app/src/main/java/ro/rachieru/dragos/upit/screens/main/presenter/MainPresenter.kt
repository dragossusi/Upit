package ro.rachieru.dragos.upit.screens.main.presenter

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachieru.dragos.upit.screens.main.view.MainActivity
import ro.rachierudragos.upitapi.UpitApi

class MainPresenter(
    val api: UpitApi,
    val viewDelegate: MainActivity
) : Presenter(), IMainPresenter {

    override fun getMyUserDetails(context: Context) {
        doIfHasInternet(
            context,
            api.getMyUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewDelegate::onUserDetails, viewDelegate::onError),
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}