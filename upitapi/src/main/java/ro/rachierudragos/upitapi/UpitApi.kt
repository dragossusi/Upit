package ro.rachierudragos.upitapi

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ro.rachierudragos.upitapi.entities.request.LoginRequest
import ro.rachierudragos.upitapi.entities.request.RegisterRequest
import ro.rachierudragos.upitapi.entities.response.*

interface UpitApi {

    @FormUrlEncoded
    @POST("token")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("device_token") token: String,
        @Field("grant_type") type: String = "password"
    ): Single<TokenResponse>

    @POST("register")
    fun register(@Body body: RegisterRequest): Single<AuthResponse>

    @GET("api/offers")
    fun getJobs(
        @Query("skip") skip: Int = 0,
        @Query("take") limit: String? = null
    ): Single<OffersResponse>

    @GET("jobs/{eventId}")
    fun getJobDetails(@Path("jobId") eventId: Int): Single<NewsResponse>

    @Multipart
    @POST("events")
    fun addNews(@Part parts: List<MultipartBody.Part>): Single<List<NewsResponse>>

    @GET("news")
    fun getNews(@Query("startFrom") startFrom: String?): Single<List<NewsResponse>>

    @GET("jobs/{eventId}")
    fun getNewsDetails(@Path("jobId") eventId: Int): Single<NewsResponse>

    @GET("api/account/userinfo")
    fun getMyUser(): Single<UserDetails>

    @POST("user/call")
    fun callUser(@Query("userId") userId: String): Single<CallResponse>

    @POST("user/closeCall")
    fun cancelCall(@Query("userId") userId: String): Single<CallResponse>

    @GET("")
    fun getJobDetails(@Path("jobId") jobId: String): Single<JobsResponse>

}

val upitApiModule = module {

    single<UpitApi> {
        val builder = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(get()))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        Retrofit.Builder()
            .client(builder)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("http://77.81.104.130:8082")//todo change me
            .build()
            .create()
    }

}
