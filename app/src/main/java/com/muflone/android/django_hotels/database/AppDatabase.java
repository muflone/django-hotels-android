package com.muflone.android.django_hotels.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.models.Brand;

@Database(entities = {Brand.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "hotels.sqlite";
    private static AppDatabase INSTANCE;
    public abstract BrandDao brandDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = android.arch.persistence.room.Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                // allow queries on the main thread.
                // Don’t do this on a real app! See PersistenceBasicSample for an example.
                //.allowMainThreadQueries()
                .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
        }
        INSTANCE = null;
    }
}
