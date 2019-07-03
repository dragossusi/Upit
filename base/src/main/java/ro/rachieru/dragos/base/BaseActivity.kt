package ro.rachieru.dragos.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.UpitApi

abstract class BaseActivity<P : IPresenter> : AppCompatActivity(), ViewDelegate {

    val localSaving: LocalSaving by inject()

    protected lateinit var presenter: P
        private set

    abstract fun initPresenter(api: UpitApi): P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter(get())
    }

    override fun onError(e: Throwable) {
        Toast.makeText(this, e.message ?: getString(R.string.error_general), Toast.LENGTH_LONG).show()
    }

    override fun onNoInternetConnection() {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show()
    }
}