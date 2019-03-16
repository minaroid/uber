package com.minageorge.uber

import android.app.Application
import com.minageorge.uber.store.room.UberDataBase
import com.minageorge.uber.ui.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class UberApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@UberApplication))
        bind() from singleton { UberDataBase(instance()) }
        bind() from provider { HomeViewModelFactory(instance(),instance()) }
    }

}