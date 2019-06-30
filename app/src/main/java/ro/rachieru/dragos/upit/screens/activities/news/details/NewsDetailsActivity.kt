package ro.rachieru.dragos.upit.screens.activities.news.details

import android.os.Bundle
import android.os.PersistableBundle
import androidx.databinding.DataBindingUtil
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.base.extensions.gone
import ro.rachieru.dragos.base.extensions.visible
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityNewsDetailsBinding
import ro.rachieru.dragos.upit.utils.BUNDLE_RESOURCE_ID
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.NewsResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class NewsDetailsActivity : BaseActivity<NewsDetailsPresenter>(), ProgressViewDelegate {

    private var newsId: Int = 0
    private lateinit var _binding: ActivityNewsDetailsBinding

    override fun initPresenter(api: UpitApi): NewsDetailsPresenter {
        return NewsDetailsPresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_news_details)
        setSupportActionBar(_binding.toolbar)
        newsId = intent.getIntExtra(BUNDLE_RESOURCE_ID, 0)
        presenter.getNewsDetails(this, newsId)
    }

    fun onNewsDetails(news: NewsResponse) {
        supportActionBar!!.title = news.title
        _binding.collapse.title = news.title
        _binding.toolbar.title = news.title
        _binding.textDetails.text = news.description
    }

    override fun showProgress() {
        _binding.progressCircular.visible()
    }

    override fun hideProgress() {
        _binding.progressCircular.gone()
    }
}