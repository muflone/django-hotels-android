package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Command;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface CommandDao {
    @Query("SELECT * " +
           "FROM commands " +
           "ORDER BY id")
    List<Command> listAll();

    @Query("SELECT * " +
           "FROM commands " +
           "WHERE id = :id")
    Command findById(Long id);

    @Query("SELECT COUNT(*) " +
           "FROM commands")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Command item);

    @Insert(onConflict = IGNORE)
    void insert(Command... items);

    @Update
    void update(Command item);

    @Update
    void update(Command... items);

    @Delete
    void delete(Command item);

    @Delete
    void delete(Command... items);

    @Query("DELETE FROM commands")
    void truncate();
}