package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Timestamp;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface TimestampDao {
    @Query("SELECT * " +
           "FROM timestamps")
    List<Timestamp> listAll();

    @Query("SELECT * " +
           "FROM timestamps " +
           "ORDER BY date DESC, time DESC " +
           "LIMIT :count")
    List<Timestamp> listByLatest(long count);

    @Query("SELECT * " +
           "FROM timestamps " +
           "WHERE contract_id = :contractId")
    List<Timestamp> listByName(long contractId);

    @Query("SELECT * " +
            "FROM timestamps " +
            "WHERE contract_id = :contractId " +
            "  AND direction_id = :directionId")
    Timestamp findById(long contractId, String directionId);

    @Query("SELECT COUNT(*) " +
           "FROM timestamps")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Timestamp item);

    @Insert(onConflict = IGNORE)
    void insert(Timestamp... items);

    @Delete
    void delete(Timestamp item);

    @Delete
    void delete(Timestamp... items);

    @Query("DELETE FROM timestamps")
    void truncate();
}