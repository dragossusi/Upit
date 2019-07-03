package ro.rachieru.dragos.upit.screens.fragments.jobs

import android.content.Intent
import ro.rachieru.dragos.base.fragment.RefreshListFragment
import ro.rachieru.dragos.upit.screens.activities.job.JobDetailsActivity
import ro.rachieru.dragos.upit.screens.fragments.jobs.adapter.JobsAdapter
import ro.rachieru.dragos.upit.utils.BUNDLE_RESOURCE_ID
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.JobsResponse
import ro.rachierudragos.upitapi.entities.response.OfferResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class JobsFragment : RefreshListFragment<OfferResponse, JobsPresenter>() {

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        presenter.getJobs(requireContext(), totalItemsCount)
    }

    override val adapter = JobsAdapter {
        startActivity(Intent(requireContext(), JobDetailsActivity::class.java).putExtra(BUNDLE_RESOURCE_ID, it.offerID))
    }

    override fun initPresenter(api: UpitApi): JobsPresenter {
        return JobsPresenter(api, this)
    }

    override fun onRefresh() {
        presenter.getJobs(requireContext())
    }

    override fun onResume() {
        super.onResume()
        if (isDataNeeded) {
            presenter.getJobs(requireContext())
        }
    }
}