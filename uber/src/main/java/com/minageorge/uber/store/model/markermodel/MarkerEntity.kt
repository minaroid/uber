package com.minageorge.uber.store.model.markermodel

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class MarkerEntity(
        @NonNull
        @PrimaryKey
        val id: String,
        val oldLatitude: String,
        val oldLongitude: String,
        val newLatitude: String,
        val newLongitude: String){
                constructor() :this("","","","","")
}