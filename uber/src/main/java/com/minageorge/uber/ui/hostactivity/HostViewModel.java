package com.minageorge.uber.ui.hostactivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minageorge.uber.store.model.markeritem.MarkerEntity;
import com.minageorge.uber.store.model.markeritem.MarkerItemMapper;
import com.minageorge.uber.store.room.UberRoomStore;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HostViewModel extends ViewModel {

    private static final String TAG = HostViewModel.class.getSimpleName();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MarkerItemMapper markerItemMapper = new MarkerItemMapper();
    private MutableLiveData<List<MarkerEntity>> markersLiveData = new MutableLiveData<>();
    private UberRoomStore uberRoomStore;

    HostViewModel(UberRoomStore uberRoomStore) {
        this.uberRoomStore = uberRoomStore;
    }

    public void fetchDataFromNetwork() {
        disposable.add(RxFirebaseDatabase.observeSingleValueEvent(mDatabase)
                .map(markerItemMapper::toMarkerItems)
                .subscribe(markers -> uberRoomStore.getMarkerDao().upsert(markers)));
    }

    void fetchDataFromLocal(LatLng latLng) {
        disposable.add(uberRoomStore.getMarkerDao().getMarkers().toObservable()
                .map(markers -> markerItemMapper.toMarkerItems(markers, latLng))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(markersLiveData::setValue));
    }

    public LiveData<List<MarkerEntity>> getMarkersLiveData() {
        return markersLiveData;
    }

    public void insertValue(LatLng latLng) {
        mDatabase.push().setValue(new MarkerEntity(
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(latLng.latitude),
                String.valueOf(latLng.longitude),
                String.valueOf(latLng.latitude),
                String.valueOf(latLng.longitude)));
    }

    @Override
    protected void onCleared() {
        disposable.dispose();
        super.onCleared();
    }
}
