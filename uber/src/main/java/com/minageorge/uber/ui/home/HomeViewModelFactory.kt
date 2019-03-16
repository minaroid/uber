package com.minageorge.uber.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minageorge.uber.store.room.UberDataBase

class HomeViewModelFactory(val uberDataBase: UberDataBase,val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(uberDataBase,context) as T
    }

}