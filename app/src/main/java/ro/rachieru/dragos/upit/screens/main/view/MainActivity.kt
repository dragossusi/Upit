package ro.rachieru.dragos.upit.screens.main.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityMainBinding
import ro.rachieru.dragos.upit.screens.activities.auth.view.AuthActivity
import ro.rachieru.dragos.upit.screens.fragments.jobs.JobsFragment
import ro.rachieru.dragos.upit.screens.activities.news.view.NewsFragment
import ro.rachieru.dragos.upit.screens.main.presenter.IMainPresenter
import ro.rachieru.dragos.upit.screens.main.presenter.MainPresenter
import ro.rachieru.dragos.upit.screens.myprofile.MyProfileActivity
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.UserDetails

class MainActivity : BaseActivity<IMainPresenter>(), NavigationView.OnNavigationItemSelectedListener,
    ProgressViewDelegate {

    private lateinit var _binding: ActivityMainBinding

    private var progressDialog: ProgressDialog? = null

    override fun initPresenter(api: UpitApi): IMainPresenter {
        return MainPresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (localSaving.token == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.loading_placeholder)
        presenter.getMyUserDetails(this)
        localSaving.addOnUserChangedListener("MainActivity") { user ->
            _binding.navView.getHeaderView(0).let {
                Glide.with(this)
                    .load(user.profilePic)
                    .circleCrop()
                    .into(it.image_user)
                it.text_user_name.text = user.fullName
                it.text_user_email.text = user.email
            }
        }
    }

    fun onUserDetails(user: UserDetails) {
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, _binding.drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        _binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        _binding.navView.getHeaderView(0).let {
            Glide.with(this)
                .load(user.profilePic)
                .circleCrop()
                .into(it.image_user)
            it.text_user_name.text = user.fullName
            it.text_user_email.text = user.email
        }
        
        _binding.navView.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_view, NewsFragment())
            .commit()
    }

    override fun showProgress() {
        progressDialog = ProgressDialog.show(
            this,
            getString(R.string.logout),
            getString(R.string.just_a_moment),
            true,
            false
        )
    }

    override fun hideProgress() {
        progressDialog?.dismiss()
    }

    override fun onBackPressed() {
        if (_binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_news -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_view, NewsFragment())
                    .commit()
            }
            R.id.nav_jobs -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_view, JobsFragment())
                    .commit()
            }
            R.id.nav_my_profile -> {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_logout -> {
                presenter.logout(this)
            }
        }

        _binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNoInternetConnection() {
        super.onNoInternetConnection()
        if (!this::_binding.isInitialized) {
            finish()
        }
    }

    override fun onError(e: Throwable) {
        super.onError(e)
        if (!this::_binding.isInitialized) {
            finish()
        }
    }

    fun onLogout() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        localSaving.removeOnUserChangedListener("MainActivity")
    }

}
