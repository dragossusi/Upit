package ro.rachieru.dragos.upit.app

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ro.rachieru.dragos.base.saving.LocalSaving
import ro.rachierudragos.upitapi.TokenSaving

val upitModule = module {
    single {
        LocalSaving(androidContext())
    }
    single<TokenSaving> {
        get<LocalSaving>()
    }
}

//const val HOST = "http://teamp.go.ro:3000"