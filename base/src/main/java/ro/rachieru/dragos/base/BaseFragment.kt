package ro.rachieru.dragos.base

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.UpitApi

abstract class BaseFragment<P : IPresenter> : Fragment(), ViewDelegate {

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
}