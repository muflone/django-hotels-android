package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface TimestampDirectionDao {
    @Query("SELECT * " +
           "FROM timestamp_directions")
    List<TimestampDirection> listAll();

    @Query("SELECT * " +
           "FROM timestamp_directions " +
           "WHERE id = :id")
    TimestampDirection findById(String id);

    @Query("SELECT * " +
           "FROM timestamp_directions " +
           "WHERE description = :name")
    TimestampDirection findByName(String name);

    @Query("SELECT * " +
           "FROM timestamp_directions " +
           "WHERE type_enter = 1")
    TimestampDirection findByTypeEnter();

    @Query("SELECT * " +
           "FROM timestamp_directions " +
           "WHERE type_exit = 1")
    TimestampDirection findByTypeExit();

    @Query("SELECT COUNT(*) " +
           "FROM timestamp_directions")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(ContractType item);

    @Insert(onConflict = IGNORE)
    void insert(TimestampDirection... items);

    @Update
    void update(TimestampDirection item);

    @Update
    void update(TimestampDirection... items);

    @Delete
    void delete(TimestampDirection item);

    @Delete
    void delete(TimestampDirection... items);

    @Query("DELETE FROM timestamp_directions")
    void truncate();
}