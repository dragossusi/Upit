package ro.rachieru.dragos.upit.screens.fragments.news.details

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class NewsDetailsPresenter(
    val api: UpitApi,
    val viewDelegate: NewsDetailsActivity
) : Presenter() {

    fun getNewsDetails(context: Context, newsId: Int) {
        doIfHasInternet(
            context,
            api.getNewsDetails(eventId = newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewDelegate::onNewsDetails, viewDelegate::onError),
            onStart = viewDelegate::showProgress,
            onNoInternet = viewDelegate::onNoInternetConnection
        )
    }

}