package com.minageorge.uber.store.model.markeritem;

import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MarkerItemMapper {

    public List<MarkerEntity> toMarkerItems(List<MarkerEntity> allMarkers, LatLng latLng) {
        float[] result = new float[1];
        List<MarkerEntity> markerEntityList = new ArrayList<>();
        for (MarkerEntity marker : allMarkers) {
            Location.distanceBetween(latLng.latitude, latLng.longitude, Double.parseDouble(marker.getNewLatitude()), Double.parseDouble(marker.getNewLongitude()), result);
            if (result[0] < 1000)
                markerEntityList.add(marker);
        }
        return markerEntityList;
    }

    public List<MarkerEntity> toMarkerItems(DataSnapshot dataSnapshot) {
        List<MarkerEntity> markerEntityList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            MarkerEntity item = snapshot.getValue(MarkerEntity.class);
            markerEntityList.add(item);
        }
        return markerEntityList;
    }
}