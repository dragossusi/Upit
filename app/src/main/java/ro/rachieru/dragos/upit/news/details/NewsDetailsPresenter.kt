package ro.rachieru.dragos.upit.news.details

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.rachieru.dragos.base.Presenter
import ro.rachieru.dragos.base.ViewDelegate
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class NewsDetailsPresenter(val api: UpitApi,
                           val viewDelegate: NewsDetailsActivity) : Presenter() {

    fun getNewsDetails(newsId: Int) {
        add(
            api.getEvent(eventId = newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewDelegate::onNewsDetails,viewDelegate::onError)
        )
    }

}