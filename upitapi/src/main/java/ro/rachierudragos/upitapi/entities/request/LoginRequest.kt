package ro.rachierudragos.upitapi.entities.request

import com.google.gson.annotations.SerializedName

class LoginRequest(

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("password")
    val password: String? = null,
    @field:SerializedName("token")
    val token:String
) {
    @field:SerializedName("platform")
    val platform = "android";
}