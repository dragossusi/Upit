package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class OffersResponse(

    @field:SerializedName("offers")
    val offers: List<OfferResponse> = ArrayList(),

    @field:SerializedName("_links")
    val links: List<Any>? = null,

    @field:SerializedName("_embedded")
    val embedded: Any? = null,

    @field:SerializedName("message")
    val message: Any? = null,

    @field:SerializedName("status")
    val status: String? = null
)