package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

data class Location(

    @field:SerializedName("coordinates")
    val coordinates: List<Int?>? = null,

    @field:SerializedName("type")
    val type: String? = null
)