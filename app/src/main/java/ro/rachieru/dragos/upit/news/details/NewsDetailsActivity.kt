package ro.rachieru.dragos.upit.news.details

import android.os.Bundle
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_news_details.*
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ViewDelegate
import ro.rachieru.dragos.upit.utils.BUNDLE_RESOURCE_ID
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.NewsResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class NewsDetailsActivity : BaseActivity<NewsDetailsPresenter>(), ViewDelegate {

    private var newsId: Int = 0

    override fun initPresenter(api: UpitApi): NewsDetailsPresenter {
        return NewsDetailsPresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_news_details)
        setSupportActionBar(toolbar)
        newsId = intent.getIntExtra(BUNDLE_RESOURCE_ID, 0)
        presenter.getNewsDetails(newsId)
    }

    fun onNewsDetails(news: NewsResponse) {
        supportActionBar!!.setTitle(news.title)
        text_details.text = news.description
    }

}