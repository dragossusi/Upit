package ro.rachierudragos.upitapi

import io.reactivex.Single
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.POST
import retrofit2.http.Query

interface UpitApi {

    @POST("login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Single<Boolean>

}

val upitApiModule = module {

    single<UpitApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("http://teamp.go.ro:3000")//todo change me
            .build()
            .create()
    }

}
