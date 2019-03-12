package com.minageorge.core.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        ButterKnife.bind(this)
        onCreateActivityComponents()
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun onCreateActivityComponents()

    fun addToDisposable(newDisposable: Disposable) {
        disposable.add(newDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}