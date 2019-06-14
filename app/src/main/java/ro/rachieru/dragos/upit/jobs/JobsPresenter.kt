package ro.rachieru.dragos.upit.jobs

import ro.rachieru.dragos.base.Presenter
import ro.rachierudragos.upitapi.UpitApi

/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class JobsPresenter(val api:UpitApi,
                    val view:JobsFragment) : Presenter() {

    fun getJobs() {
        api.getJobs();
    }

}