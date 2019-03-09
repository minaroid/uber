package com.minageorge.uber.ui.hostactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.minageorge.uber.R;
import com.minageorge.uber.UberApplication;
import com.minageorge.uber.di.activity.ActivityModule;
import com.minageorge.uber.di.scope.ActivityScope;
import com.minageorge.uber.store.model.markeritem.MarkerEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@ActivityScope
public class HostActivity extends AppCompatActivity {

    @Inject
    HostViewModelFactory hostViewModelFactory;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

    private GoogleMap mainMap;
    private LocationCallback locationCallback;
    private LatLng myLocationLatLng;
    private HostViewModel hostViewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private HashMap<String, Marker> allMarkersHashMap = new HashMap<>();
    private List<Marker> allMarkersList = new ArrayList<>();
    private float zoomLevel = 15;
    private boolean isMarkerRotating;
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Snackbar snackbar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UberApplication.getComponent(this)
                .plus(new ActivityModule(this)).inject(this);

        ButterKnife.bind(this);
        hostViewModel = ViewModelProviders.of(this, hostViewModelFactory).get(HostViewModel.class);
        hostViewModel.getMarkersLiveData().observe(this, this::renderMarkers);
        setUpMainMap();
        locationCallback = createLocationCallback();
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(this).get())
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper());
        checkNetworkState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @OnClick(R.id.im_menu)
    void onMenuClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.im_gps)
    void onMyLocationClicked() {
        if (myLocationLatLng != null) {
            CameraPosition target = CameraPosition.builder()
                    .target(myLocationLatLng)
                    .zoom(zoomLevel)
                    .build();
            mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));
            for (Marker marker : allMarkersHashMap.values()) {
                marker.setIcon(BitmapDescriptorFactory.
                        fromBitmap(resizeMapIcons(BitmapFactory.decodeResource(getResources(), R.drawable.car),
                                (int) (zoomLevel * 3), (int) (zoomLevel * 5.5))));
            }
        }
    }

    private void setUpMainMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_main);
        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
//            googleMap.setOnMapClickListener(hostViewModel::insertValue);
            googleMap.setOnCameraMoveStartedListener(level -> {
                for (Marker marker : allMarkersHashMap.values()) {
                    marker.setIcon(BitmapDescriptorFactory.
                            fromBitmap(resizeMapIcons(BitmapFactory.decodeResource(getResources(), R.drawable.car),
                                    (int) (googleMap.getCameraPosition().zoom * 3), (int) (googleMap.getCameraPosition().zoom * 5.5))));
                }
            });
            mainMap = googleMap;
        });
    }

    private void renderMarkers(List<MarkerEntity> markerEntities) {
        mainMap.clear();
        allMarkersList.clear();
        allMarkersHashMap.clear();
        mainMap.addMarker(new MarkerOptions()
                .position(myLocationLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.main_menu_location_icon)));
        for (MarkerEntity markerEntity : markerEntities) {
            Marker marker = mainMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .position(new LatLng(Double.parseDouble(markerEntity.getNewLatitude()), Double.parseDouble(markerEntity.getNewLongitude())))
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(BitmapFactory.decodeResource(getResources(), R.drawable.car), (int) (zoomLevel * 3), (int) (zoomLevel * 5.5)))));
            allMarkersHashMap.put(markerEntity.getId(), marker);
            Log.d("MarkerKey ", markerEntity.getId());
        }

        allMarkersList.addAll(allMarkersHashMap.values());
        disposable.add(Observable.interval(5, TimeUnit.SECONDS)
                .subscribe(v -> handler.post(() -> rotateMarker(allMarkersList.get(random.nextInt(allMarkersList.size())), random.nextInt(350)))));
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location result = locationResult.getLastLocation();
                if (result != null) {
                    Log.d("CurrentLocation-Lat", String.valueOf(result.getLatitude()));
                    Log.d("CurrentLocation-Lon", String.valueOf(result.getLongitude()));
                    myLocationLatLng = new LatLng(result.getLatitude(), result.getLongitude());
                    CameraPosition target = CameraPosition.builder()
                            .target(myLocationLatLng)
                            .zoom(zoomLevel)
                            .build();
                    mainMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    hostViewModel.fetchDataFromLocal(myLocationLatLng);
                }
            }
        };
    }

    public Bitmap resizeMapIcons(Bitmap icon, int width, int height) {
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }

    private void rotateMarker(final Marker marker, final float toRotation) {

        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 4000;
            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    float rot = t * toRotation + (1 - t) * startRotation;
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private void checkNetworkState() {
        disposable.add(ReactiveNetwork
                .observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.available()) {
                        hostViewModel.fetchDataFromNetwork();
                        if (snackbar != null) {
                            snackbar.dismiss();
                        }
                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "Can't reach the Uber network", Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        snackbar.show();
                    }
                }));
    }
}
