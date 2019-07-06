package ro.rachierudragos.upitapi

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ro.rachierudragos.upitapi.entities.request.UserDetailsRequest
import ro.rachierudragos.upitapi.entities.response.*

interface UpitApi {

    @FormUrlEncoded
    @POST("token")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("device_token") token: String,
        @Field("grant_type") type: String = "password",
        @Field("platform") platform: String = "android"
    ): Single<TokenResponse>

    //jobs
    @GET("api/offers")
    fun getJobs(
        @Query("skip") skip: Int = 0,
        @Query("take") limit: Int? = null
    ): Single<OffersResponse>

    @GET("api/offers/{jobId}")
    fun getJobDetails(@Path("jobId") jobId: Int): Single<OfferResponse>

    @GET("api/news")
    fun getNews(
        @Query("skip") skip: Int = 0,
        @Query("take") limit: Int? = null
    ): Single<NewsListResponse>

    //user

    @GET("api/account/userinfo")
    fun getMyUser(): Single<UserDetails>

    @GET("api/account/userinfo")
    fun saveUserDetails(@Body details: UserDetailsRequest): Single<UserDetails>

    @Multipart
    @POST("api/Account/UploadProfilePhoto")
    fun changeAvatar(@Part image: MultipartBody.Part): Single<String>

    @Multipart
    @POST("api/Account/UploadCV")
    fun uploadCV(@Part cv: MultipartBody.Part): Single<String>

    @POST("api/Account/Logout")
    fun logout(): Completable

    @PUT("api/offers/apply/{id}")
    fun apply(@Path("id") id: Int):Single<OfferResponse>

    //call

    @POST("api/call")
    fun callUser(@Body body: CallRequest): Single<CallResponse>

    @POST("api/call/close")
    fun cancelCall(@Body body: CallRequest): Single<CallResponse>

}

const val HOST = "https://api.interviewvideochatmodule.ro:8083"

val upitApiModule = module {

    single<UpitApi> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = getUnsafeOkHttpClientBuilder()
            .addInterceptor(TokenInterceptor(get()))
            .addInterceptor(loggingInterceptor)
            .build()
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl(HOST)//todo change me
            .build()
            .create()
    }

}
