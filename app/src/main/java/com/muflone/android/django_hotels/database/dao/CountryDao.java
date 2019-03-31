package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Country;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface CountryDao {
    @Query("SELECT * " +
           "FROM countries " +
           "ORDER BY name")
    List<Country> listAll();

    @Query("SELECT * " +
           "FROM countries " +
           "WHERE id = :id")
    Country findById(String id);

    @Query("SELECT * " +
           "FROM countries " +
           "WHERE name = :name")
    Country findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM countries")
    long count();

    @Insert(onConflict = IGNORE)
    void insert(Country item);

    @Insert(onConflict = IGNORE)
    void insert(Country... items);

    @Update
    void update(Country item);

    @Update
    void update(Country... items);

    @Delete
    void delete(Country item);

    @Delete
    void delete(Country... items);

    @Query("DELETE FROM countries")
    void truncate();
}
