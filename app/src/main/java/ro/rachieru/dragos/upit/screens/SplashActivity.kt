package ro.rachieru.dragos.upit.screens

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ro.rachieru.dragos.upit.R
import android.content.Intent
import android.os.Handler
import ro.rachieru.dragos.upit.screens.main.view.MainActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 2000)
    }

}