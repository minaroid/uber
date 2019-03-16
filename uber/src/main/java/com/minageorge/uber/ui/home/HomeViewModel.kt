package com.minageorge.uber.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.minageorge.core.base.BaseViewModel
import com.minageorge.uber.R
import com.minageorge.uber.store.model.markermodel.MarkerEntity
import com.minageorge.uber.store.room.UberDataBase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HomeViewModel(private val uberDataBase: UberDataBase, private val context: Context) : BaseViewModel() {

    private val mDatabase = FirebaseDatabase.getInstance().reference
    val markersLiveData: MutableLiveData<ArrayList<MarkerEntity>> = MutableLiveData()
    var isNetworkAvailable: Boolean = true

    fun insertValue(latLng: LatLng) {
        mDatabase.push().setValue(MarkerEntity(
                System.currentTimeMillis().toString(),
                latLng.latitude.toString(),
                latLng.longitude.toString(),
                latLng.latitude.toString(),
                latLng.longitude.toString()))
    }

    fun fetchDataFromLocal(latLng: LatLng) {
        addToDisposable(uberDataBase.getMarkerDao().getMarkers().toObservable()
                .map {
                    Log.d(TAG, "Load markers from local on ${Thread.currentThread().name}")
                    val markersList: ArrayList<MarkerEntity> = ArrayList()
                    for (marker in it) {
                        val result = FloatArray(1)
                        Location.distanceBetween(latLng.latitude, latLng.longitude, marker.newLatitude.toDouble(), marker.newLongitude.toDouble(), result)
                        if (result[0] < 1000) {
                            markersList.add(marker)
                        }
                    };markersList
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(markersLiveData::setValue))
    }

    fun fetchDataFromNetwork() {
        addToDisposable(RxFirebaseDatabase.observeSingleValueEvent(mDatabase)
                .map {
                    val markers: ArrayList<MarkerEntity> = ArrayList()
                    for (snapshot in it.children) {
                        val marker = snapshot.getValue(MarkerEntity::class.java)
                        markers.add(marker!!)
                    };markers
                }
                .observeOn(Schedulers.io())
                .subscribe(uberDataBase.getMarkerDao()::upsert, this::processError))
    }

    private fun processError(t: Throwable) {

    }

    companion object {
        const val TAG: String = "HOME-VIEW-MODEL"
    }

}