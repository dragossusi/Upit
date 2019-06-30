package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class NewsResponse(

    @field:SerializedName("picture_path")
    val picturePath: String? = null,

    @field:SerializedName("newsID")
    val newsID: Int? = null,

    @field:SerializedName("university_domain")
    val universityDomain: String? = null,

    @field:SerializedName("_links")
    val links: List<Any?>? = null,

    @field:SerializedName("_embedded")
    val embedded: Any? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("create_date")
    val createDate: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("created_by")
    val createdBy: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)