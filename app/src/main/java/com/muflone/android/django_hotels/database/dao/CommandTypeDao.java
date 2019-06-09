package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.CommandType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface CommandTypeDao {
    @Query("SELECT * " +
           "FROM command_types " +
           "ORDER BY id")
    List<CommandType> listAll();

    @Query("SELECT * " +
           "FROM command_types " +
           "WHERE id = :id")
    CommandType findById(String id);

    @Query("SELECT COUNT(*) " +
           "FROM command_types")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(CommandType item);

    @Insert(onConflict = IGNORE)
    void insert(CommandType... items);

    @Update
    void update(CommandType item);

    @Update
    void update(CommandType... items);

    @Delete
    void delete(CommandType item);

    @Delete
    void delete(CommandType... items);

    @Query("DELETE FROM command_types")
    void truncate();
}