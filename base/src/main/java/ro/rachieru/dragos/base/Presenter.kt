package ro.rachieru.dragos.base

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ro.rachieru.dragos.base.extensions.hasActiveInternetConnection

abstract class Presenter/*<V : ViewDelegate>*/ : IPresenter/*<V>*/ {

//    override lateinit var viewDelegate: V

    private val compositeDisposable = CompositeDisposable()

    fun add(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun clear() {
        compositeDisposable.clear()
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    operator fun plusAssign(d: Disposable) {
        add(d)
    }

    override fun doIfHasInternet(
        context: Context,
        d: Disposable,
        onStart: (() -> Unit)?,
        onNoInternet: (() -> Unit)?
    ) {
        if (context.hasActiveInternetConnection()) {
            onStart?.invoke()
            add(d)
        } else {
            onNoInternet?.invoke()
        }
    }

}