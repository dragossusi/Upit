package ro.rachieru.dragos.upit.screens.fragments.jobs.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_job.view.*
import ro.rachieru.dragos.base.extensions.inflate
import ro.rachieru.dragos.upit.R
import ro.rachierudragos.upitapi.entities.response.OfferResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
class JobsViewHolder(
    parent: ViewGroup,
    val onJobClicked: OnJobClicked
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(parent.inflate(R.layout.item_job)),
    View.OnClickListener {

    private lateinit var _job: OfferResponse

    override fun onClick(v: View?) {
        onJobClicked(_job)
    }

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(job: OfferResponse) {
        _job = job
        val image = job.documents?.firstOrNull()?.let {
            //            val string = "$HOST/$it"
            Log.d("job image", it.path)
            it.path
        }

        Glide.with(itemView)
            .load(image)
            .into(itemView.image_job)
        itemView.text_title.text = job.title
        itemView.text_subtitle.text = job.description
    }

}