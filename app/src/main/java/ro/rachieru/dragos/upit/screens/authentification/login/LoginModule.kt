package ro.rachieru.dragos.upit.screens.authentification.login

import org.koin.dsl.module.module
import ro.rachieru.dragos.upit.screens.authentification.login.presenter.ILoginPresenter
import ro.rachieru.dragos.upit.screens.authentification.login.presenter.LoginPresenter
import ro.rachieru.dragos.upit.screens.authentification.login.view.ILoginViewDelegate
import ro.rachieru.dragos.upit.screens.authentification.login.view.LoginActivity

val loginModule = module {

    factory<ILoginPresenter> { (view: ILoginViewDelegate) ->
        LoginPresenter(get(), view)
    }

}