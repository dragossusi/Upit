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

    fun getNews(context: Context, start: Int = 0) {
        doIfHasInternet(
            context,
            api.getNews(start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewDelegate.hideProgress()
                    viewDelegate.onItems(it.news ?: ArrayList(), start == 0)
                }, {
                    viewDelegate.hideProgress()
                    viewDelegate.onError(it)
                }),
            onStart = viewDelegate::hideProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}