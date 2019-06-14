package ro.rachieru.dragos.upit.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ro.rachierudragos.upitapi.upitApiModule

class UpitApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@UpitApp)
            modules(
                listOf(
                    upitModule,
                    upitApiModule
                )
            )
        }
    }

}