package com.minageorge.core.utils.rxmap

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.Function

class CameraMoveFunc : Function<GoogleMap, Observable<Unit>> {

    override fun apply(t: GoogleMap): Observable<Unit> {
        return Observable.create { subscriber ->
            t.setOnCameraMoveListener {
                subscriber.onNext(Unit)
            }
        }
    }

}