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
import ro.rachierudragos.upitapi.entities.response.AuthResponse
import ro.rachierudragos.upitapi.entities.response.CallResponse
import ro.rachierudragos.upitapi.entities.response.NewsResponse
import ro.rachierudragos.upitapi.entities.response.JobsResponse

@Deprecated("new api", ReplaceWith("UpitApi", "ro.rachierudragos.upitapi.UpitApi"))
interface UpitApiNode {

    @FormUrlEncoded
    @POST("login")
    fun login(@Body body: LoginRequest): Single<AuthResponse>

    @POST("register")
    fun register(@Body body: RegisterRequest): Single<AuthResponse>

    @GET("jobs")
    fun getJobs(@Query("startFrom") startFrom: String?): Single<List<JobsResponse>>

    @GET("jobs/{eventId}")
    fun getJobDetails(@Path("jobId") eventId: Int): Single<NewsResponse>

    @Multipart
    @POST("events")
    fun addNews(@Part parts: List<MultipartBody.Part>): Single<List<NewsResponse>>

    @GET("news")
    fun getNews(@Query("startFrom") startFrom: String?): Single<List<NewsResponse>>

    @GET("jobs/{eventId}")
    fun getNewsDetails(@Path("jobId") eventId: Int): Single<NewsResponse>

    @GET("user/")
    fun getMyUser(): Single<UserDetails>

    @POST("user/call")
    fun callUser(@Query("userId") userId: String): Single<CallResponse>

    @POST("user/closeCall")
    fun cancelCall(@Query("userId") userId: String): Single<CallResponse>

    @GET("")
    fun getJobDetails(@Path("jobId") jobId: String): Single<JobsResponse>

}

val upitApiNodeModule = module {

    single<UpitApiNode> {
        val builder = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(get()))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        Retrofit.Builder()
            .client(builder)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("http://teamp.go.ro:3000")//todo change me
            .build()
            .create()
    }

}
