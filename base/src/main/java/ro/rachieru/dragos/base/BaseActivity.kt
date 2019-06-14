package ro.rachieru.dragos.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.UpitApi

abstract class BaseActivity<P : IPresenter> : AppCompatActivity(), ViewDelegate {

    val api: UpitApi by inject()

    val localSaving: LocalSaving by inject()

    protected lateinit var presenter: P
        private set

    abstract fun initPresenter(api: UpitApi): P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter(api)
    }

    override fun onError(e: Throwable) {
        Toast.makeText(this, e.message ?: getString(R.string.error_general), Toast.LENGTH_LONG).show()
    }
}