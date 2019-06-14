package ro.rachierudragos.upitapi

import com.google.gson.annotations.SerializedName

class UserDetails(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("phone")
    var phone: String? = null
)