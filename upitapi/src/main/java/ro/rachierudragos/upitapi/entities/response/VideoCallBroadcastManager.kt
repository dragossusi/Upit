package ro.rachierudragos.upitapi.entities.response

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.Intent
import androidx.annotation.StringDef
import androidx.localbroadcastmanager.content.LocalBroadcastManager


/**
 * Upit
 *
 * @author Dragos
 * @since 13.06.2019
 */
object VideoCallBroadcastManager {


    @StringDef(CallStatus.DECLINED, CallStatus.UNKNOWN)
    annotation class CallStatus {
        companion object {
            const val DECLINED = "declined"
            const val UNKNOWN = "unknown"
        }
    }

    fun sendStatus(context: Context, @CallStatus state: String) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        val intent = Intent(VIDEO_CALL_FILTER)
        intent.putExtra(VIDEO_CALL_EXTRA, state)
        localBroadcastManager.sendBroadcast(intent)
    }

    fun registerBroadcast(context: Context, receiver: BroadcastReceiver) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context)
            .registerReceiver(
                receiver,
                IntentFilter(VIDEO_CALL_FILTER)
            )
    }

    fun unregisterBroadcast(context: Context, receiver: BroadcastReceiver) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    private val VIDEO_CALL_FILTER = "video-call-filter"
    val VIDEO_CALL_EXTRA = "video-call-extra"

}