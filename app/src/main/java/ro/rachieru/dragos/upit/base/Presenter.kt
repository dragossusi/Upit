package ro.rachieru.dragos.upit.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter : IPresenter {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(d: Disposable) {
        compositeDisposable.add(d)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}