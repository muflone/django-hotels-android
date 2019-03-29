package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface TimestampDao {
    @Query("SELECT * " +
           "FROM timestamps")
    List<Timestamp> listAll();

    @Query("SELECT " +
           "  employees.first_name, " +
           "  employees.last_name, " +
           "  timestamps.date, " +
           "  timestamps.time, " +
           "  timestamp_directions.description AS direction " +
           "FROM timestamps " +
           "INNER JOIN contracts " +
           "   ON contracts.id = timestamps.contract_id " +
           "INNER JOIN employees " +
           "   ON employees.id = contracts.employee_id " +
           "INNER JOIN timestamp_directions " +
           "   ON timestamp_directions.id = timestamps.direction_id " +
           "ORDER BY timestamps.date DESC, timestamps.time DESC " +
           "LIMIT :count")
    List<TimestampEmployee> listByLatest(long count);

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