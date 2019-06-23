package ro.rachierudragos.upitapi.entities.response

import com.google.gson.annotations.SerializedName

class OfferResponse(

	@field:SerializedName("documents")
	val documents: List<DocumentResponse?>? = null,

	@field:SerializedName("_links")
	val links: List<Any?>? = null,

	@field:SerializedName("_embedded")
	val embedded: Any? = null,

	@field:SerializedName("offerID")
	val offerID: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("message")
	val message: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)