package com.minageorge.uber.store.model.markermodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(markers: List<MarkerEntity>)

    @Query("select * from markers")
    fun getMarkers(): Flowable<List<MarkerEntity>>
}