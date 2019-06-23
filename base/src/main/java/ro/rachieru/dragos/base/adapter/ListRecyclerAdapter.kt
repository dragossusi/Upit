package ro.rachieru.dragos.base.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
abstract class ListRecyclerAdapter<T, VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>(
    private val _items: MutableList<T> = ArrayList()
) : androidx.recyclerview.widget.RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int {
        return _items.size
    }

    val items: List<T>
        get() = _items

    fun add(item: T) {
        _items.add(item)
    }

    fun addAll(items: Collection<T>) {
        _items.addAll(items)
    }

    fun clear() {
        _items.clear()
    }

    val size: Int
        get() = _items.size

    val isEmpty: Boolean
        get() = _items.isEmpty()

}