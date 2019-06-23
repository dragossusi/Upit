package ro.rachieru.dragos.upit.screens.fragments.login.presenter

import android.content.Context
import ro.rachieru.dragos.base.IPresenter

interface ILoginPresenter : IPresenter {
    fun login(context: Context, email: String, password: String)
}
