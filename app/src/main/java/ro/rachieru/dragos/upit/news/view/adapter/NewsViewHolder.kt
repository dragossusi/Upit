package ro.rachieru.dragos.upit.news.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_news.view.*
import ro.rachieru.dragos.base.extensions.inflate
import ro.rachieru.dragos.upit.R
import ro.rachierudragos.upitapi.entities.response.NewsResponse

class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_news)) {

    fun bind(news: NewsResponse) {
        itemView.run {
            Glide.with(image_news)
                .load(news.images!!.firstOrNull())
                .into(image_news)
            text_title.text = news.title
            text_subtitle.text = news.description
        }
    }

}