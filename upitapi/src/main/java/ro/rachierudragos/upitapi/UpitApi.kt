package ro.rachierudragos.upitapi

import io.reactivex.Single
import okhttp3.MultipartBody
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ro.rachierudragos.upitapi.entities.request.LoginRequest
import ro.rachierudragos.upitapi.entities.request.RegisterRequest
import ro.rachierudragos.upitapi.entities.response.AuthResponse
import ro.rachierudragos.upitapi.entities.response.CallResponse
import ro.rachierudragos.upitapi.entities.response.NewsResponse
import ro.rachierudragos.upitapi.entities.response.JobsResponse

interface UpitApi {
    @POST("login")
    fun login(@Body body: LoginRequest): Single<AuthResponse>

    @POST("register")
    fun register(@Body body: RegisterRequest): Single<AuthResponse>

    @GET("events")
    fun getEvents(): Single<List<NewsResponse>>

    @GET("events/{eventId}")
    fun getEvent(@Path("eventId") eventId: Int): Single<NewsResponse>

    @Multipart
    @POST("events")
    fun addEvent(@Part parts: List<MultipartBody.Part>): Single<List<NewsResponse>>

    @GET("jobs")
    fun getJobs(): List<JobsResponse>

    @GET("user/")
    fun getMyUser(): Single<UserDetails>

    @POST("user/call")
    fun callUser(@Query("userId") userId: String): Single<CallResponse>

    @POST("user/closeCall")
    fun cancelCall(@Query("userId") userId: String): Single<CallResponse>

}

val upitApiModule = module {

    single<UpitApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("http://192.168.1.4:3000")//todo change me
            .build()
            .create()
    }

}
