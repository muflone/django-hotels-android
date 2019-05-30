package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Room;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface RoomDao {
    @Query("SELECT * " +
           "FROM rooms " +
           "ORDER BY name")
    List<Room> listAll();

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE building_id = :buildingId " +
           "ORDER BY name")
    List<Room> listByBuilding(long buildingId);

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE id = :id")
    Room findById(long id);

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE name = :name")
    Room findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM rooms")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Room item);

    @Insert(onConflict = IGNORE)
    void insert(Room... items);

    @Update
    void update(Room item);

    @Update
    void update(Room... items);

    @Delete
    void delete(Room item);

    @Delete
    void delete(Room... items);

    @Query("DELETE FROM rooms")
    void truncate();
}