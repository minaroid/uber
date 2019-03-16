package com.minageorge.core.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.google.android.gms.maps.GoogleMap
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        ButterKnife.bind(this)
        onCreateActivityComponents()
        observeNetWorkState()
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun onCreateActivityComponents()

    abstract fun observeNetWorkState()

    fun addToDisposable(newDisposable: Disposable) {
        disposable.add(newDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}
