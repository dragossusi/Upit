package ro.rachieru.dragos.upit.saving

import android.content.Context
import com.google.gson.Gson
import ro.rachierudragos.upitapi.UserDetails

class LocalSaving(private val context: Context) {

    val gson = Gson()

    var userId: Int
        get() = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .getInt(PREF_USER_ID, -1)
        set(value) {
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREF_USER_ID, value)
                .apply()
        }

    var user: UserDetails?
        get() {
            val details: UserDetails? = gson.fromJson(
                context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    .getString(PREF_USER, ""), UserDetails::class.java
            )
            details?.id = userId
            return details
        }
        set(value) {
            userId = value?.id ?: -1
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_USER_ID, gson.toJson(value))
                .apply()
        }

    companion object {
        private const val SHARED_PREF_NAME = "local-saving"

        private const val PREF_USER = "user"
        private const val PREF_USER_ID = "user_id"
    }

}