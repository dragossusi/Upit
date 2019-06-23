package ro.rachierudragos.upitapi

import com.google.gson.annotations.SerializedName

class UserDetails(

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("profilePic")
    val profilePic: String? = null,

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("loginProvider")
    val loginProvider: Any? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("hasRegistered")
    val hasRegistered: Boolean? = null
)