package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class CallRequest(

    @field:SerializedName("receiver_user_id")
    val receiverUserId: String? = null
)