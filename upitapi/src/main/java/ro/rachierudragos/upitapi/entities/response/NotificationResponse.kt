package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(

    @field:SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @field:SerializedName("alert")
    val alert: String? = null,

    @field:SerializedName("from")
    val from: UserDetails? = null,

    @field:SerializedName("UUID")
    val uUID: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("chatRoom")
    val chatRoom: String? = null
)