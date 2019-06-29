package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

@Deprecated("not used")
class UserDetails(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("avatarPath")
    val avatarPath: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("cv")
    val cv: String? = null
)