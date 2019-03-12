package com.minageorge.core.utils.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MapObservableProvider(supportMapFragment: SupportMapFragment) {

    val mapReadyObservable = BehaviorSubject.create<GoogleMap>()

    init {
        val observable = Observable.create<GoogleMap> {
            subscriber-> supportMapFragment.getMapAsync { subscriber.onNext(it) }
        }
        observable.subscribe(mapReadyObservable)
    }

}