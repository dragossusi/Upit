package ro.rachieru.dragos.upit.screens.fragments.news.view

import ro.rachieru.dragos.base.fragment.RefreshListFragment
import ro.rachieru.dragos.upit.screens.fragments.news.view.adapter.NewsAdapter
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.NewsResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class NewsFragment : RefreshListFragment<NewsResponse, NewsPresenter>() {

    override val adapter = NewsAdapter()

    override fun initPresenter(api: UpitApi): NewsPresenter {
        return NewsPresenter(api, this)
    }

    override fun onRefresh() {
        presenter.getNews(requireContext())
    }

    override fun onResume() {
        super.onResume()
        if (isDataNeeded) {
            presenter.getNews(requireContext())
        }
    }

}