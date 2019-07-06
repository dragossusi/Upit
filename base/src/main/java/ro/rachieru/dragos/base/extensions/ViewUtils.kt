package ro.rachieru.dragos.base.extensions

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Upit
 *
 * @author Dragos
 * @since 30.06.2019
 */

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}