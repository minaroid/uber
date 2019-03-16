package com.minageorge.uber.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.minageorge.core.base.BaseActivity
import com.minageorge.core.utils.rxmap.MapObservableProvider
import com.minageorge.uber.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel

    private lateinit var mapObservableProvider: MapObservableProvider
    private lateinit var locationCallback: LocationCallback
    private var myLocationLatLng: LatLng? = null
    private val allMarkers: ArrayList<Marker> = ArrayList()
    override fun getLayout() = R.layout.activity_home

    @SuppressLint("MissingPermission")
    override fun onCreateActivityComponents() {

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_main) as SupportMapFragment

        // init map Observable provider and map config .
        mapObservableProvider = MapObservableProvider(mapFragment).also { provider ->
            addToDisposable(provider.mapReadyObservable.subscribe {
                it.uiSettings.isRotateGesturesEnabled = false
                it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            })
        }

        locationCallback = createLocationCallback()

        LocationServices.getFusedLocationProviderClient(WeakReference<Context>(this).get()!!)
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper())

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        viewModel.markersLiveData.observe(this, Observer { markerModels ->
            addToDisposable(mapObservableProvider.mapReadyObservable.subscribe {
                it.clear()
                allMarkers.clear()
                val zoomLevel = it.cameraPosition.zoom
                for (item in markerModels)
                    allMarkers.add(it.addMarker(MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .flat(true)
                            .position(LatLng(item.newLatitude.toDouble(), item.newLongitude.toDouble()))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(
                                    BitmapFactory.decodeResource(resources, R.drawable.car), (zoomLevel * 3).toInt(),
                                    (zoomLevel * 5.5).toInt())))))
            })
        })

        addToDisposable(mapObservableProvider.getCameraZoomChangedObservable().subscribe {
            for (marker in allMarkers) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(
                        BitmapFactory.decodeResource(resources, R.drawable.car), (it * 3).toInt(),
                        (it * 5.5).toInt())))
            }
        })

        addToDisposable(mapObservableProvider.getCameraIdleObservable()
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewModel.fetchDataFromLocal(it)
                })
        addToDisposable(mapObservableProvider.getMapClickObservable().subscribe{
            viewModel.insertValue(it)
        })
    }

    override fun observeNetWorkState() {
        addToDisposable(ReactiveNetwork
                .observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    viewModel.isNetworkAvailable = connectivity.available()
                    if (connectivity.available()) {
                        viewModel.fetchDataFromNetwork()
                        Toast.makeText(this, "Network available ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Network not available ", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val result = locationResult!!.lastLocation
                if (result != null) {
                    myLocationLatLng = LatLng(result.latitude, result.longitude)
                    addToDisposable(mapObservableProvider.mapReadyObservable.subscribe {
                        val target = CameraPosition.builder()
                                .target(myLocationLatLng)
                                .zoom(16F)
                                .build()
                        it.moveCamera(CameraUpdateFactory.newCameraPosition(target))
                    })
                    viewModel.fetchDataFromLocal(myLocationLatLng!!)
                }
            }
        }
    }

    private fun resizeMapIcons(icon: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(icon, width, height, false)
    }
}


