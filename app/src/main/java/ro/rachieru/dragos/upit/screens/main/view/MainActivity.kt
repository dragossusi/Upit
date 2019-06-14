package ro.rachieru.dragos.upit.screens.main.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import ro.rachieru.dragos.upit.screens.activities.auth.view.AuthActivity
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.upit.databinding.ActivityMainBinding
import ro.rachieru.dragos.upit.news.view.NewsFragment
import ro.rachieru.dragos.upit.screens.main.presenter.IMainPresenter
import ro.rachieru.dragos.upit.screens.main.presenter.MainPresenter
import ro.rachierudragos.upitapi.UpitApi

class MainActivity : BaseActivity<IMainPresenter>(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var _binding: ActivityMainBinding

    override fun initPresenter(api: UpitApi): IMainPresenter {
        return MainPresenter(api)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (localSaving.userId == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Nu stiu daca ne trebuie asta", Snackbar.LENGTH_LONG)
                .setAction("???", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

/*        localSaving.user?.let {user->
            nav_view.getHeaderView(0).let {
//                it.image_user
                it.text_user_name.text = user.name
                it.text_user_email.text = user.email
            }
        }*/
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
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
            R.id.nav_settings -> {

            }
            R.id.nav_logout -> {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
