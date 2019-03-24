package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Location;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface LocationDao {
    @Query("SELECT * " +
           "FROM locations " +
           "ORDER BY name")
    List<Location> getAll();

    @Query("SELECT * " +
           "FROM locations " +
           "WHERE id = :id")
    Location findById(long id);

    @Query("SELECT * " +
           "FROM locations " +
           "WHERE name = :name")
    Location findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM locations")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Location item);

    @Insert(onConflict = IGNORE)
    void insert(Location... items);

    @Delete
    void delete(Location item);

    @Delete
    void delete(Location... items);

    @Query("DELETE FROM locations")
    void truncate();
}