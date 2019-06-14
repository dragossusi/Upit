package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class AuthResponse(

    @field:SerializedName("jwt")
    val jwt: String? = null,

    @field:SerializedName("user")
    val user: UserDetails
)