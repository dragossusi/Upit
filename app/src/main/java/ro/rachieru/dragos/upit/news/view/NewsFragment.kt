package ro.rachieru.dragos.upit.news.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_news.*
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.news.view.adapter.NewsAdapter

class NewsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_news.layoutManager = LinearLayoutManager(requireContext())
        recycler_news.adapter = NewsAdapter()
    }

}