package ro.rachieru.dragos.upit.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ro.rachieru.dragos.upit.call.CalledByUserActivity
import ro.rachierudragos.upitapi.entities.response.UserDetails
import ro.rachierudragos.upitapi.entities.response.VideoCallBroadcastManager


/**
 * Upit
 *
 * @author Dragos
 * @since 06.06.2019
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    val gson = Gson()

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage!!.data

        if (!data.containsKey("type"))
            data["type"] = "call"

        if (data.containsKey("type")) {
            when (data["type"]) {
//                "chat_message" -> {
//                    val message = data["message"]
//                    val model = gson.fromJson(message, ChatNotificationModel::class.java)
//                    if (Integer.parseInt(LocalSaving.getChatRoomId(applicationContext)) != model.getChatRoomId()) {
//                        mNotificationsPresenter.displayChatMessageNotification(data)
//                    }
//                }

                "call" -> {
                    Log.d(this.javaClass.name, "Video call= $data")
                    if (data.containsKey("action")) {
                        when (data["action"]) {
                            "send" -> {
                                val from = gson.fromJson<UserDetails>(data["fromUser"], UserDetails::class.java)
                                CalledByUserActivity.startActivity(
                                    this,
                                    from.id!!,
                                    from.id!!,
                                    from.id!!,
                                    from.name!!,
                                    data["chatRoom"]!!,
                                    from.avatarPath
                                )
                            }
                            "rejected" -> VideoCallBroadcastManager.sendStatus(
                                this,
                                VideoCallBroadcastManager.CallStatus.DECLINED
                            )
                        }
                    }
                }
            }
        }
    }
}