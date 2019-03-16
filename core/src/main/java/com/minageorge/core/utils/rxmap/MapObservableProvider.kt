package com.minageorge.core.utils.rxmap

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MapObservableProvider {

    val mapReadyObservable = BehaviorSubject.create<GoogleMap>()

    constructor(supportMapFragment: SupportMapFragment) {
        val observable = Observable.create<GoogleMap> { subscriber ->
            supportMapFragment.getMapAsync {
                subscriber.onNext(it)
            }
        }
        observable.subscribe(mapReadyObservable)
    }

    fun getMapClickObservable(): Observable<LatLng> {
        return mapReadyObservable.flatMap(MapClickFunc())
    }

    fun getMapLongClickObservable(): Observable<LatLng> {
        return mapReadyObservable.flatMap(MapLongClickFunc())
    }

    fun getCameraIdleObservable(): Observable<LatLng> {
        return mapReadyObservable.flatMap(CameraIdleFunc())
    }

    fun getCameraMoveObservable(): Observable<Unit> {
        return mapReadyObservable.flatMap(CameraMoveFunc())
    }

    fun getCameraMoveCanceledObservable(): Observable<Unit> {
        return mapReadyObservable.flatMap(CameraMoveCanceledFunc())
    }

    fun getCameraMoveStartedObservable(): Observable<Int> {
        return mapReadyObservable.flatMap(CameraMoveStartedFunc())
    }

    fun getCameraZoomChangedObservable(): Observable<Float> {
        return mapReadyObservable.flatMap(CameraZoomChangedFunc())
    }

    fun getMarkerClickObservable(): Observable<Marker> {
        return mapReadyObservable.flatMap(MarkerClickFunc())
    }
}