package ro.rachieru.dragos.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ro.rachieru.dragos.base.BaseFragment
import ro.rachieru.dragos.base.IPresenter
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.base.R
import ro.rachieru.dragos.base.adapter.ListRecyclerAdapter
import ro.rachierudragos.upitapi.EndlessRecyclerViewScrollListener

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
abstract class RefreshListFragment<T, P : IPresenter> : BaseFragment<P>(),
    ProgressViewDelegate,
    SwipeRefreshLayout.OnRefreshListener {

    protected abstract val adapter: ListRecyclerAdapter<T, *>

    protected var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    protected var recyclerView: androidx.recyclerview.widget.RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflated = inflater.inflate(R.layout.fragment_refresh_list, container, false)
        swipeRefresh = inflated.findViewById(R.id.swipe_refresh)
        recyclerView = inflated.findViewById(R.id.recycler_items)
        return inflated;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh!!.setOnRefreshListener(this)
        recyclerView!!.let {
            val manager = LinearLayoutManager(requireContext())
            it.layoutManager = manager
            it.addOnScrollListener(object : EndlessRecyclerViewScrollListener(manager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    this@RefreshListFragment.onLoadMore(page, totalItemsCount)
                }
            })
            it.adapter = adapter
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

    fun onItems(items: Collection<T>, refresh: Boolean) {
        if (refresh) {
            adapter.clear()
            adapter.addAll(items)
            adapter.notifyDataSetChanged()
        } else {
            adapter.addAll(items)
            adapter.notifyItemRangeInserted(adapter.size - items.size, items.size)
        }
    }

    override fun showProgress() {
        swipeRefresh?.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh?.isRefreshing = false
    }

    protected val isDataNeeded: Boolean
        get() = adapter.isEmpty

}