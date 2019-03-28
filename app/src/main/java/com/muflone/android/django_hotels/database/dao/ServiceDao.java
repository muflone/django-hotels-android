package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Service;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface ServiceDao {
    @Query("SELECT * " +
           "FROM services " +
           "ORDER BY name")
    List<Service> listAll();

    @Query("SELECT * " +
           "FROM services " +
           "WHERE id = :id")
    Service findById(long id);

    @Query("SELECT * " +
           "FROM services " +
           "WHERE name = :name")
    Service findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM services")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Service item);

    @Insert(onConflict = IGNORE)
    void insert(Service... items);

    @Delete
    void delete(Service item);

    @Delete
    void delete(Service... items);

    @Query("DELETE FROM services")
    void truncate();
}