package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName
import ro.rachierudragos.upitapi.UserDetails

class CallResponse(

    @field:SerializedName("optionalName")
    val chatRoom: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("user")
    val user: UserDetails? = null,

    @field:SerializedName("message")
    val message: String? = null

) {
    val isSuccess: Boolean
        get() = status != null && status == "Success"
}