package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class DocumentResponse(

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("_links")
    val links: List<Any?>? = null,

    @field:SerializedName("_embedded")
    val embedded: Any? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("message")
    val message: Any? = null,

    @field:SerializedName("status")
    val status: String? = null
)