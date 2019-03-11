package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Brand;

import java.util.List;

@Dao
public interface BrandDao {
    @Query("SELECT * FROM brands")
    List<Brand> getAll();

    @Query("SELECT * FROM brands where id = :id")
    Brand findById(int id);

    @Query("SELECT * FROM brands where name = :name")
    Brand findByName(String name);

    @Query("SELECT COUNT(*) from brands")
    int count();

    @Insert
    void insertAll(Brand... brands);

    @Delete
    void delete(Brand brand);
}