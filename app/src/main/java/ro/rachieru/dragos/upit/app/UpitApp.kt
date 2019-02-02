package ro.rachieru.dragos.upit.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.android.startKoin
import ro.rachieru.dragos.upit.screens.authentification.login.loginModule

class UpitApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        startKoin(
            androidContext = this,
            modules = listOf(
                loginModule,
                activityBuilderModule
            )
        )
    }

}