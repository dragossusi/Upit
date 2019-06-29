package ro.rachierudragos.upitapi

import com.google.gson.annotations.SerializedName

class UserDetails(

//    @get:Bindable
    @field:SerializedName("firstName")
    var firstName: String? = null,

//    @get:Bindable
    @field:SerializedName("lastName")
    var lastName: String? = null,

//    @get:Bindable
    @field:SerializedName("profilePic")
    var profilePic: String? = null,

//    @get:Bindable
    @field:SerializedName("fullName")
    var fullName: String? = null,

//    @get:Bindable
    @field:SerializedName("loginProvider")
    var loginProvider: Any? = null,

//    @get:Bindable
    @field:SerializedName("email")
    var email: String? = null,

//    @get:Bindable
    @field:SerializedName("hasRegistered")
    var hasRegistered: Boolean? = null
) //: BaseObservable()