package ro.rachieru.dragos.upit.screens.authentification.login.view

import ro.rachieru.dragos.upit.base.IBaseViewDelegate
import ro.rachierudragos.upitapi.UserDetails

interface ILoginViewDelegate : IBaseViewDelegate {
    fun onLoggedIn(details: UserDetails)


}