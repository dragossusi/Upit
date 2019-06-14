package org.appspot.apprtc.util

import android.content.SharedPreferences
import android.app.Activity
import android.util.Log
import android.content.DialogInterface
import android.app.AlertDialog
import android.webkit.URLUtil
import android.webkit.URLUtil.isHttpUrl
import android.webkit.URLUtil.isHttpsUrl
import org.appspot.apprtc.R


/**
 * Upit
 *
 * @author Dragos
 * @since 13.06.2019
 */
fun sharedPrefGetString(
    activity: Activity, sharedPref: SharedPreferences,
    attributeId: Int, defaultId: Int
): String? {
    val defaultValue = activity.getString(defaultId)
    val attributeName = activity.getString(attributeId)
    return sharedPref.getString(attributeName, defaultValue)
}

fun sharedPrefGetBoolean(
    activity: Activity, sharedPref: SharedPreferences,
    attributeId: Int, defaultId: Int
): Boolean {
    val defaultValue = java.lang.Boolean.valueOf(activity.getString(defaultId))
    val attributeName = activity.getString(attributeId)
    return sharedPref.getBoolean(attributeName, defaultValue)
}

fun sharedPrefGetInteger(
    activity: Activity, sharedPref: SharedPreferences,
    attributeId: Int, defaultId: Int
): Int {
    val defaultString = activity.getString(defaultId)
    val defaultValue = Integer.parseInt(defaultString)

    val attributeName = activity.getString(attributeId)
    val value = sharedPref.getString(attributeName, defaultString)
    try {
        return Integer.parseInt(value!!)
    } catch (e: NumberFormatException) {
        Log.e("SharedPrefGet", "Wrong setting for: $attributeName:$value")
        return defaultValue
    }

}

fun validateUrl(activity: Activity, url: String): Boolean {
    if (isHttpsUrl(url) || isHttpUrl(url)) {
        return true
    }

    AlertDialog.Builder(activity)
        .setTitle(activity.getText(R.string.invalid_url_title))
        .setMessage(activity.getString(R.string.invalid_url_text, url))
        .setCancelable(false)
        .setNeutralButton(R.string.ok,
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        .create()
        .show()
    return false
}