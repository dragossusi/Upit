package ro.rachieru.dragos.upit.app

import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ro.rachieru.dragos.upit.api.UpitApi

val activityBuilderModule = module {

    single<UpitApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("teamp.go.ro:3000")//todo change me
            .build()
            .create()
    }

}