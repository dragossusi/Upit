package ro.rachieru.dragos.upit.app

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import ro.rachieru.dragos.upit.saving.LocalSaving

val upitModule = module {
    single {
        LocalSaving(androidContext())
    }
}