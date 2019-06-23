package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class TokenResponse(

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName(".expires")
    val expires: String? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("expires_in")
    val expiresIn: Int? = null,

    @field:SerializedName(".issued")
    val issued: String? = null
)