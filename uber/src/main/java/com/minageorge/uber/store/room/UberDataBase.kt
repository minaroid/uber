package com.minageorge.uber.store.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minageorge.uber.store.model.markermodel.MarkerDao
import com.minageorge.uber.store.model.markermodel.MarkerEntity

@Database(entities = [MarkerEntity::class], version = 1)
abstract class UberDataBase : RoomDatabase() {

    abstract fun getMarkerDao(): MarkerDao

    companion object {
        @Volatile
        private var instance: UberDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        UberDataBase::class.java, "uber.db")
                        .build()
    }
}