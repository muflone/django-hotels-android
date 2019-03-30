package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Brand;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface BrandDao {
    @Query("SELECT * " +
           "FROM brands " +
           "ORDER BY name")
    List<Brand> listAll();

    @Query("SELECT * " +
           "FROM brands " +
           "WHERE id = :id")
    Brand findById(long id);

    @Query("SELECT * " +
           "FROM brands " +
           "WHERE name = :name")
    Brand findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM brands")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Brand item);

    @Insert(onConflict = IGNORE)
    void insert(Brand... items);

    @Update
    void update(Brand item);

    @Update
    void update(Brand... items);

    @Delete
    void delete(Brand item);

    @Delete
    void delete(Brand... items);

    @Query("DELETE FROM brands")
    void truncate();
}