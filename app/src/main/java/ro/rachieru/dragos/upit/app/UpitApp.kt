package ro.rachieru.dragos.upit.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ro.rachierudragos.upitapi.upitApiModule
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil.showErrorNotification
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.google.android.gms.security.ProviderInstaller.ProviderInstallListener
import com.google.android.gms.security.ProviderInstaller
import ro.rachieru.dragos.videocall.util.SslUtils


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
        upgradeSecurityProvider()
        SslUtils.disableSSLCertificateChecking()
    }

    private fun upgradeSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstallListener {
            override fun onProviderInstalled() {

            }

            override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent) {
                //        GooglePlayServicesUtil.showErrorNotification(errorCode, MainApplication.this);
                GoogleApiAvailability.getInstance().showErrorNotification(this@UpitApp, errorCode)
            }
        })
    }

}