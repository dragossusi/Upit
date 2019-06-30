package ro.rachierudragos.upitapi.entities.request

import com.google.gson.annotations.SerializedName

/**
 * Upit
 *
 * @author Dragos
 * @since 30.06.2019
 */
class UserDetailsRequest(

    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String

)