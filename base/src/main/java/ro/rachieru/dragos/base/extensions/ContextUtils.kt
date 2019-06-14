package ro.rachieru.dragos.base.extensions

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.net.ConnectivityManager


fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

fun Context.hasActiveInternetConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val isConnected = connectivityManager != null &&
            connectivityManager.activeNetworkInfo != null &&
            connectivityManager.activeNetworkInfo.isConnected
    return isConnected
}