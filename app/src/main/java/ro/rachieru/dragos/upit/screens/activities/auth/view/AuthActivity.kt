package ro.rachieru.dragos.upit.screens.activities.auth.view

import android.os.Bundle
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.upit.screens.activities.auth.presenter.AuthPresenter
import ro.rachieru.dragos.upit.screens.activities.auth.presenter.IAuthPresenter
import ro.rachieru.dragos.upit.screens.fragments.login.view.LoginFragment
import ro.rachierudragos.upitapi.UpitApi

class AuthActivity : BaseActivity<IAuthPresenter>() {

    override fun initPresenter(api: UpitApi): IAuthPresenter {
        return AuthPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, LoginFragment())
            .commit()
    }
}
