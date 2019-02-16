package ro.rachieru.dragos.upit.screens.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.koin.android.ext.android.inject
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityMainBinding
import ro.rachieru.dragos.upit.news.view.NewsFragment
import ro.rachieru.dragos.upit.saving.LocalSaving
import ro.rachieru.dragos.upit.screens.authentification.login.view.LoginActivity
import ro.rachierudragos.upitapi.UserDetails

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var _binding: ActivityMainBinding

    private val localSaving: LocalSaving by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (localSaving.userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
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
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
