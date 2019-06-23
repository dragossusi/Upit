package ro.rachieru.dragos.upit.screens.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_my_profile.*
import ro.rachieru.dragos.base.BaseFragment
import ro.rachieru.dragos.upit.R
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.UserDetails

/**
 * Upit
 *
 * @author Dragos
 * @since 09.06.2019
 */
class MyProfileFragment : BaseFragment<MyProfilePresenter>() {
    override fun initPresenter(api: UpitApi): MyProfilePresenter {
        return MyProfilePresenter(api, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    fun onUserDetails(it: UserDetails) {
        Glide.with(this)
            .load(it.profilePic)
            .into(image_profile)
        text_email.text = it.email
        text_name.text = it.fullName
    }
}