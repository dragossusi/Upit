package ro.rachieru.dragos.base

import android.content.Context
import androidx.fragment.app.Fragment
import android.widget.Toast
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.UpitApiNode

abstract class BaseFragment<P : IPresenter> : androidx.fragment.app.Fragment(), ViewDelegate {

    protected val api: UpitApi by inject()

    protected val localSaving: LocalSaving by inject()

    protected lateinit var presenter: P
        private set

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter = initPresenter(api)
    }

    override fun onPause() {
        super.onPause()
        presenter.clear()
    }

    abstract fun initPresenter(api: UpitApi): P

    override fun onError(e: Throwable) {
        context?.let {
            Toast.makeText(it, e.message ?: getString(R.string.error_general), Toast.LENGTH_LONG).show()
        }
    }

    override fun onNoInternetConnection() {
        context?.let {
            Toast.makeText(it, R.string.no_internet, Toast.LENGTH_LONG).show()
        }
    }
}