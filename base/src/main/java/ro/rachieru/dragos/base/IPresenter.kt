package ro.rachieru.dragos.base

interface IPresenter /*<V:ViewDelegate>*/ {
    //    var viewDelegate: V
    fun dispose()

    fun clear()
}