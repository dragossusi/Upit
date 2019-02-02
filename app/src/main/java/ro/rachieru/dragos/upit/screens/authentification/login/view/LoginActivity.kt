package ro.rachieru.dragos.upit.screens.authentification.login.view

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.FragmentLoginBinding
import ro.rachieru.dragos.upit.screens.authentification.login.presenter.ILoginPresenter

class LoginActivity : Activity(), ILoginViewDelegate, View.OnClickListener {
    val presenter: ILoginPresenter by inject()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onError(e: Throwable) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }
}