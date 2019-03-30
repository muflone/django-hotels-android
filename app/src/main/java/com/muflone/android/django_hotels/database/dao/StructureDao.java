package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Structure;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface StructureDao {
    @Query("SELECT * " +
           "FROM structures " +
           "ORDER BY name")
    List<Structure> listAll();

    @Query("SELECT * " +
           "FROM structures " +
           "WHERE id = :id")
    Structure findById(long id);

    @Query("SELECT * " +
           "FROM structures " +
           "WHERE name = :name")
    Structure findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM structures")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Structure item);

    @Insert(onConflict = IGNORE)
    void insert(Structure... items);

    @Update
    void update(Structure item);

    @Update
    void update(Structure... items);

    @Delete
    void delete(Structure item);

    @Delete
    void delete(Structure... items);

    @Query("DELETE FROM structures")
    void truncate();
}