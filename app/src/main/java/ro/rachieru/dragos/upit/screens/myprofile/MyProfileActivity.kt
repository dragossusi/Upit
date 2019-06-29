package ro.rachieru.dragos.upit.screens.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_profile.*
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.BaseFragment
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityMyProfileBinding
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.UserDetails

/**
 * Upit
 *
 * @author Dragos
 * @since 09.06.2019
 */
class MyProfileActivity : BaseActivity<MyProfilePresenter>() {

    private lateinit var _binding: ActivityMyProfileBinding

    override fun initPresenter(api: UpitApi): MyProfilePresenter {
        return MyProfilePresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);
    }

    fun onUserDetails(it: UserDetails) {
        _binding.user = it
        Glide.with(this)
            .load(it.profilePic)
            .centerCrop()
            .into(_binding.imageProfile)
    }
}