package com.minageorge.core.utils.rxmap

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.Function

class CameraMoveStartedFunc : Function<GoogleMap, Observable<Int>> {
    override fun apply(t: GoogleMap): Observable<Int> {
        return Observable.create { subscriber ->
            t.setOnCameraMoveStartedListener {
                subscriber.onNext(it)
            }
        }
    }

}