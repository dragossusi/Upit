package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class JobsResponse(

    @field:SerializedName("images")
    val images: List<String>? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("priority")
    val priority: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null

) {
    @Expose
    var pagerPosition: Int = 0
}