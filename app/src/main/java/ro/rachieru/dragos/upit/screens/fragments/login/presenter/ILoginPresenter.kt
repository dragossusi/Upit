package ro.rachieru.dragos.upit.screens.fragments.login.presenter

import ro.rachieru.dragos.base.IPresenter

interface ILoginPresenter:IPresenter {
    fun login(email: String, password: String)
}
