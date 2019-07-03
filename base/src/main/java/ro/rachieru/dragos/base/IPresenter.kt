package ro.rachieru.dragos.base

import android.content.Context
import io.reactivex.disposables.Disposable

interface IPresenter /*<V:ViewDelegate>*/ {
    //    var viewDelegate: V
    fun dispose()

    fun clear()

    fun doIfHasInternet(
        context: Context,
        d: Disposable,
        onStart: (() -> Unit)? = null,
        onNoInternet: (() -> Unit)? = null
    )
}