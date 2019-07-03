package ro.rachieru.dragos.base

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.UpitApi

abstract class BaseFragment<P : IPresenter> : Fragment(), ViewDelegate {

    protected val localSaving: LocalSaving by inject()

    protected lateinit var presenter: P
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = initPresenter(get())
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