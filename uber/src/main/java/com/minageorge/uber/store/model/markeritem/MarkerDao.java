package com.minageorge.uber.store.model.markeritem;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Flowable;

@Dao
public interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(List<MarkerEntity> markers);

    @Query("select * from marker")
    Flowable<List<MarkerEntity>> getMarkers();

}
