package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Building;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface BuildingDao {
    @Query("SELECT * " +
           "FROM buildings " +
           "ORDER BY name")
    List<Building> listAll();

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE structure_id = :structureId")
    List<Building> listByStructure(long structureId);

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE id = :id")
    Building findById(long id);

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE name = :name")
    Building findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM buildings")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Building item);

    @Insert(onConflict = IGNORE)
    void insert(Building... items);

    @Update
    void update(Building item);

    @Update
    void update(Building... items);

    @Delete
    void delete(Building item);

    @Delete
    void delete(Building... items);

    @Query("DELETE FROM buildings")
    void truncate();
}