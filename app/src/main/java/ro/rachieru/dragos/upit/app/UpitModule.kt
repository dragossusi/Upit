package ro.rachieru.dragos.upit.app

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ro.rachieru.dragos.base.saving.LocalSaving

val upitModule = module {
    single {
        LocalSaving(androidContext())
    }
}