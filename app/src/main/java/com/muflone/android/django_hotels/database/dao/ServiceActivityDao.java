package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface ServiceActivityDao {
    @Query("SELECT * " +
           "FROM activities")
    List<ServiceActivity> listAll();

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE transmission IS NULL " +
           "ORDER BY activities.date ASC")
    List<ServiceActivity> listByUntrasmitted();

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND room_id < 0 " +
           "ORDER BY room_id DESC")
    List<ServiceActivity> listExtrasByDateContract(@NonNull Date date, long contractId);

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND room_id > 0 " +
           "ORDER BY room_id")
    List<ServiceActivity> listByDateContract(@NonNull Date date, long contractId);

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND room_id = :roomId")
    List<ServiceActivity> listByDateContract(@NonNull Date date, long contractId, long roomId);

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE id = :id")
    ServiceActivity findById(long id);

    @Query("SELECT COUNT(*) " +
           "FROM activities")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(ServiceActivity item);

    @Insert(onConflict = IGNORE)
    void insert(ServiceActivity... items);

    @Update
    void update(ServiceActivity item);

    @Update
    void update(ServiceActivity... items);

    @Delete
    void delete(ServiceActivity item);

    @Delete
    void delete(ServiceActivity... items);

    @Query("DELETE FROM activities")
    void truncate();
}