package ro.rachieru.dragos.upit.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.Query

interface UpitApi {

    @POST("login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Single<Boolean>

}