package ro.rachieru.dragos.upit.news.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_news.view.*
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.inflate
import ro.rachieru.dragos.upit.pojos.News

class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_news)) {

    fun bind(news: News) {
        itemView.run {
            image_news.setImageURI(news.image)
            text_title.text = news.title
            text_subtitle.text = news.subtitle
        }
    }

}