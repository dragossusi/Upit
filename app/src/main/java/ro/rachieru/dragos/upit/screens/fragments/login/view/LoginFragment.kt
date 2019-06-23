package ro.rachieru.dragos.upit.screens.fragments.login.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.BaseFragment
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.screens.fragments.login.presenter.ILoginPresenter
import ro.rachieru.dragos.upit.screens.fragments.login.presenter.LoginPresenter
import ro.rachieru.dragos.upit.screens.main.view.MainActivity
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.TokenResponse

class LoginFragment : BaseFragment<ILoginPresenter>(), LoginViewDelegate, View.OnClickListener {
    override fun initPresenter(api: UpitApi): ILoginPresenter {
        return LoginPresenter(api, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        presenter.login(v.context, edit_username.text.toString(), edit_password.text.toString())
    }

    override fun onLoginSuccess(t: TokenResponse) {
        (requireActivity() as BaseActivity<*>).apply {
            localSaving.token = t.accessToken
//            localSaving.userId = t.user.id
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun showProgress() {
        btn_login?.visibility = View.GONE
        progress_login?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        btn_login?.visibility = View.VISIBLE
        progress_login?.visibility = View.GONE
    }
}