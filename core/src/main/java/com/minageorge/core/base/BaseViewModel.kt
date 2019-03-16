package com.minageorge.core.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    private val disposable = CompositeDisposable()

    fun addToDisposable(newDisposable: Disposable) {
        disposable.add(newDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}