package ro.rachieru.dragos.base.saving

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ro.rachierudragos.upitapi.TokenSaving
import ro.rachierudragos.upitapi.UserDetails

class LocalSaving(private val context: Context) : TokenSaving {

    val gson = Gson()

    val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override var token: String?
        get() = preferences
            .getString(PREF_TOKEN, null)
        set(value) {
            preferences.edit()
                .putString(PREF_TOKEN, value)
                .apply()
        }

    var userId: String?
        get() = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .getString(PREF_USER_ID, null)
        set(value) {
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_USER_ID, value)
                .apply()
        }

    var user: UserDetails?
        get() {
            val details: UserDetails? = gson.fromJson(
                context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    .getString(PREF_USER, ""), UserDetails::class.java
            )
//            details?.id = userId!!
            return details
        }
        set(value) {
//            userId = value?.id
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_USER_ID, gson.toJson(value))
                .apply()
        }

    companion object {
        private const val SHARED_PREF_NAME = "local-saving"

        private const val PREF_USER = "user"
        private const val PREF_USER_ID = "user_id"
        private const val PREF_TOKEN = "pref-token"
    }

}