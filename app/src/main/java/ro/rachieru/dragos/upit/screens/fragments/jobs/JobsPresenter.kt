package ro.rachieru.dragos.upit.screens.fragments.jobs

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class JobsPresenter(
    val api: UpitApi,
    val view: JobsFragment
) : Presenter() {

    fun getJobs(context: Context, start: Int = 0) {
        doIfHasInternet(
            context = context,
            d = api.getJobs(start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.hideProgress()
                    view.onItems(it.offers, start == 0)
                }, {
                    view.hideProgress()
                    view.onError(it)
                }),
            onStart = view::showProgress,
            onNoInternet = view::onNoInternetConnection
        )
    }

}