package ro.rachieru.dragos.upit.screens.fragments.login.view

import ro.rachieru.dragos.base.ViewDelegate
import ro.rachierudragos.upitapi.entities.response.AuthResponse

interface LoginViewDelegate : ViewDelegate {

    fun onLoginSuccess(t: AuthResponse)

}
