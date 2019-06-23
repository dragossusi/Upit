package ro.rachieru.dragos.upit.screens.fragments.news.view.adapter

import android.view.ViewGroup
import ro.rachieru.dragos.base.adapter.ListRecyclerAdapter
import ro.rachierudragos.upitapi.entities.response.NewsResponse

class NewsAdapter : ListRecyclerAdapter<NewsResponse, NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(p0: NewsViewHolder, p1: Int) {
        p0.bind(items[p1])
    }
}