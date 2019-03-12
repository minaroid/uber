package com.minageorge.uber.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.minageorge.core.base.BaseActivity
import com.minageorge.core.utils.map.MapObservableProvider
import com.minageorge.uber.R
import java.lang.ref.WeakReference

class HomeActivity : BaseActivity() {

    private lateinit var mapObservableProvider: MapObservableProvider
    private lateinit var locationCallback: LocationCallback
    private lateinit var viewModel: HomeViewModel
    private var myLocationLatLng: LatLng? = null

    override fun getLayout() = R.layout.activity_home

    @SuppressLint("MissingPermission")
    override fun onCreateActivityComponents() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_main) as SupportMapFragment

        mapObservableProvider = MapObservableProvider(mapFragment).also { provider ->
            addToDisposable(provider.mapReadyObservable.subscribe {
                it.getUiSettings().setRotateGesturesEnabled(false)
                it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            })
        }

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        locationCallback = createLocationCallback()
        LocationServices.getFusedLocationProviderClient(WeakReference<Context>(this).get()!!)
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper())
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val result = locationResult!!.lastLocation
                if (result != null) {
                    Log.d("CurrentLocation-Lat", result.latitude.toString())
                    Log.d("CurrentLocation-Lon", result.longitude.toString())
                    myLocationLatLng = LatLng(result.latitude, result.longitude)
                    addToDisposable(mapObservableProvider.mapReadyObservable.subscribe {
                        val target = CameraPosition.builder()
                                .target(LatLng(result.latitude, result.longitude))
                                .zoom(zoomLevel)
                                .build()
                        it.moveCamera(CameraUpdateFactory.newCameraPosition(target))
                    })
                }
            }
        }
    }

    companion object {
        const val zoomLevel: Float = 16F
    }
}


