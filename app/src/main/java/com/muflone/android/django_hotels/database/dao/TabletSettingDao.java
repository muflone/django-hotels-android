package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.TabletSetting;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface TabletSettingDao {
    @Query("SELECT * " +
           "FROM settings " +
           "ORDER BY name")
    List<TabletSetting> listAll();

    @Query("SELECT * " +
           "FROM settings " +
           "WHERE name = :name")
    TabletSetting findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM settings")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(TabletSetting item);

    @Insert(onConflict = IGNORE)
    void insert(TabletSetting... items);

    @Update
    void update(TabletSetting item);

    @Update
    void update(TabletSetting... items);

    @Delete
    void delete(TabletSetting item);

    @Delete
    void delete(TabletSetting... items);

    @Query("DELETE FROM settings")
    void truncate();
}