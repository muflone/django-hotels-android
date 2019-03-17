package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Room;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms")
    List<Room> getAll();

    @Query("SELECT * FROM rooms WHERE id = :id")
    Room findById(int id);

    @Query("SELECT * FROM rooms WHERE name = :name")
    Room findByName(String name);

    @Query("SELECT COUNT(*) FROM rooms")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Room item);

    @Insert(onConflict = IGNORE)
    void insert(Room... items);

    @Delete
    void delete(Room item);

    @Delete
    void delete(Room... items);

    @Query("DELETE FROM rooms")
    void truncate();
}