package com.minageorge.uber.store.model.markeritem;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "marker")
public class MarkerEntity {

    @NonNull
    @PrimaryKey
    private String id;
    private String oldLatitude;
    private String oldLongitude;
    private String newLatitude;
    private String newLongitude;

    @Ignore
    public MarkerEntity() {
    }

    public MarkerEntity(String id, String oldLatitude, String oldLongitude,
                        String newLatitude, String newLongitude) {
        this.oldLatitude = oldLatitude;
        this.oldLongitude = oldLongitude;
        this.newLatitude = newLatitude;
        this.newLongitude = newLongitude;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getOldLatitude() {
        return oldLatitude;
    }

    public String getOldLongitude() {
        return oldLongitude;
    }

    public String getNewLatitude() {
        return newLatitude;
    }

    public String getNewLongitude() {
        return newLongitude;
    }
}
