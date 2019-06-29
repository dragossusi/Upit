package ro.rachieru.dragos.upit.screens.activities.news.view

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
class NewsPresenter(
    val api: UpitApi,
    val viewDelegate: NewsFragment
) : Presenter() {

    fun getNews(context: Context, start: String? = null) {
        doIfHasInternet(
            context,
            api.getNews(start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onItems(it, start == null)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }), viewDelegate::hideProgress, viewDelegate::onNoInternetConnection
        )
    }

}