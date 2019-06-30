package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.CommandUsage;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface CommandUsageDao {
    @Query("SELECT * " +
           "FROM commands_usage " +
           "ORDER BY id")
    List<CommandUsage> listAll();

    @Query("SELECT * " +
           "FROM commands_usage " +
           "WHERE id = :id")
    CommandUsage findById(Long id);

    @Query("SELECT COUNT(*) " +
           "FROM commands_usage")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(CommandUsage item);

    @Insert(onConflict = IGNORE)
    void insert(CommandUsage... items);

    @Update
    void update(CommandUsage item);

    @Update
    void update(CommandUsage... items);

    @Delete
    void delete(CommandUsage item);

    @Delete
    void delete(CommandUsage... items);

    @Query("DELETE FROM commands_usage")
    void truncate();
}