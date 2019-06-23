package ro.rachieru.dragos.upit.screens.fragments.login.view

import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachierudragos.upitapi.entities.response.TokenResponse

interface LoginViewDelegate : ProgressViewDelegate {

    fun onLoginSuccess(t: TokenResponse)

}
