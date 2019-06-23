package ro.rachieru.dragos.upit.screens.fragments.jobs.adapter

import android.view.ViewGroup
import ro.rachieru.dragos.base.adapter.ListRecyclerAdapter
import ro.rachierudragos.upitapi.entities.response.JobsResponse
import ro.rachierudragos.upitapi.entities.response.OfferResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
class JobsAdapter(val onJobClicked: OnJobClicked) : ListRecyclerAdapter<OfferResponse, JobsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): JobsViewHolder {
        return JobsViewHolder(p0, onJobClicked)
    }

    override fun onBindViewHolder(p0: JobsViewHolder, p1: Int) {
        p0.bind(items[p1])
    }

}

typealias OnJobClicked = (OfferResponse) -> Unit