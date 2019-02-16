package ro.rachieru.dragos.upit.screens.authentification.login.view

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.FragmentLoginBinding
import ro.rachieru.dragos.upit.saving.LocalSaving
import ro.rachieru.dragos.upit.screens.authentification.login.presenter.ILoginPresenter
import ro.rachieru.dragos.upit.screens.authentification.login.presenter.LoginPresenter
import ro.rachieru.dragos.upit.screens.main.MainActivity
import ro.rachierudragos.upitapi.UserDetails

class LoginActivity : Activity(), ILoginViewDelegate, View.OnClickListener {
    val presenter: ILoginPresenter by inject {
        parametersOf(this as ILoginViewDelegate)
    }

    private val localSaving: LocalSaving by inject()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        presenter = LoginPresenter(get(),this)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_login)
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> presenter.login(
                binding.editUsername.text.toString(),
                binding.editPassword.text.toString()
            )
        }
    }

    override fun onLoggedIn(details: UserDetails) {
        localSaving.user = details
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onError(e: Throwable) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }
}