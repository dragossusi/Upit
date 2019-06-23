package ro.rachieru.dragos.upit.screens.main.presenter

import android.content.Context
import ro.rachieru.dragos.base.IPresenter

interface IMainPresenter : IPresenter {
    fun getMyUserDetails(context: Context)
}