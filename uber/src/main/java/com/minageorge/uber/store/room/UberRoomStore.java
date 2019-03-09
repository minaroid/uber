package com.minageorge.uber.store.room;

import android.content.Context;

import com.minageorge.uber.store.model.markeritem.MarkerDao;
import com.minageorge.uber.store.model.markeritem.MarkerEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MarkerEntity.class}, version = 1, exportSchema = false)
public abstract class UberRoomStore extends RoomDatabase {

    private static UberRoomStore INSTANCE;

    public static synchronized UberRoomStore getAppDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (UberRoomStore.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UberRoomStore.class, "Uber.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract MarkerDao getMarkerDao();

}
