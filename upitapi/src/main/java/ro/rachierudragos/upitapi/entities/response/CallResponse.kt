package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

data class CallResponse(

    @field:SerializedName("chatRoom")
    val chatRoom: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)