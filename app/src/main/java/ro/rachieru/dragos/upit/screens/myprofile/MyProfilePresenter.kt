package ro.rachieru.dragos.upit.screens.myprofile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 09.06.2019
 */
class MyProfilePresenter(val api: UpitApi,
                         val viewDelegate: MyProfileFragment) : Presenter() {

    fun getDetails() {
        add(
            api.getMyUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.onUserDetails(it)
                },{
                    viewDelegate.onError(it)
                })
        )
    }

}