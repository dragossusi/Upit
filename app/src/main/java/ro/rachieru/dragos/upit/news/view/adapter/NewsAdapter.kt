package ro.rachieru.dragos.upit.news.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ro.rachierudragos.upitapi.entities.response.NewsResponse

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>() {

    private val items = ArrayList<NewsResponse>().apply {
        add(
            NewsResponse(
                images = listOf("https://i.ytimg.com/vi/I1_A_mMQa9k/maxresdefault.jpg"),
                title = "VP in quals",
                description = "Pavaga and Winstrike are now out"
            )
        )
        add(
            NewsResponse(
                images = listOf("https://i.ytimg.com/vi/I1_A_mMQa9k/maxresdefault.jpg"),
                title = "VP in quals",
                description = "Pavaga and Winstrike are now out"
            )
        )
        add(
            NewsResponse(
                images = listOf("https://i.ytimg.com/vi/I1_A_mMQa9k/maxresdefault.jpg"),
                title = "VP in quals",
                description = "Pavaga and Winstrike are now out"
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: NewsViewHolder, p1: Int) {
        p0.bind(items[p1])
    }
}