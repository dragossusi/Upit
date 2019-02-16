package ro.rachieru.dragos.upit.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter/*<V : IBaseViewDelegate>*/ : IPresenter/*<V>*/ {

//    override lateinit var viewDelegate: V

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}