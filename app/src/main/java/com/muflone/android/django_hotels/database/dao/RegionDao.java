package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Region;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface RegionDao {
    @Query("SELECT * " +
           "FROM regions " +
           "ORDER BY name")
    List<Region> listAll();

    @Query("SELECT * " +
           "FROM regions " +
           "WHERE id = :id")
    Region findById(long id);

    @Query("SELECT * " +
           "FROM regions " +
           "WHERE name = :name")
    Region findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM regions")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Region item);

    @Insert(onConflict = IGNORE)
    void insert(Region... items);

    @Delete
    void delete(Region item);

    @Delete
    void delete(Region... items);

    @Query("DELETE FROM regions")
    void truncate();
}