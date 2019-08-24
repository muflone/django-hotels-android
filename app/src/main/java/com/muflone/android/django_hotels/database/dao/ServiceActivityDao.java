package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.muflone.android.django_hotels.database.models.ReportActivityDetail;
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
           "  AND extras = 0 " +
           "ORDER BY activities.date ASC")
    List<ServiceActivity> listByUntransmitted();

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE transmission IS NULL " +
           "  AND extras = 1 " +
           "ORDER BY activities.date ASC")
    List<ServiceActivity> listExtrasByUntransmitted();

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND extras = 1 " +
           "ORDER BY room_id ASC")
    List<ServiceActivity> listExtrasByDateContract(@NonNull Date date, long contractId);

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND extras = 0 " +
           "ORDER BY room_id ASC")
    List<ServiceActivity> listByDateContract(@NonNull Date date, long contractId);

    @Query("SELECT * " +
           "FROM activities " +
           "WHERE date = :date " +
           "  AND contract_id = :contractId " +
           "  AND room_id = :roomId")
    List<ServiceActivity> listByDateContract(@NonNull Date date, long contractId, long roomId);

    @Query("SELECT " +
            "  companies.id AS company_id, " +
            "  companies.name AS company, " +
            "  employees.id AS employee_id, " +
            "  employees.first_name, " +
            "  employees.last_name, " +
            "  contracts.id AS contract_id, " +
            "  activities.date AS datetime, " +
            "  buildings.id AS building_id, " +
            "  buildings.name AS building, " +
            "  rooms.id AS room_id, " +
            "  rooms.name AS room, " +
            "  services.id AS service_id, " +
            "  services.name AS service, " +
            "  activities.description " +
            "FROM activities " +
            "INNER JOIN contracts " +
            "   ON contracts.id = activities.contract_id " +
            "INNER JOIN companies " +
            "   ON companies.id = contracts.company_id " +
            "INNER JOIN employees " +
            "   ON employees.id = contracts.employee_id " +
            "INNER JOIN rooms " +
            "   ON rooms.id = activities.room_id " +
            "INNER JOIN buildings " +
            "   ON buildings.id = rooms.building_id " +
            "INNER JOIN services " +
            "   ON services.id = activities.service_id " +
            "WHERE activities.structure_id = :structureId " +
            "  AND activities.date = :date " +
            "ORDER BY companies.name ASC, " +
            "         employees.first_name ASC, " +
            "         employees.last_name ASC, " +
            "         activities.date ASC, " +
            "         buildings.name ASC, " +
            "         rooms.name ASC, " +
            "         services.name ASC")
    List<ReportActivityDetail> listForReportActivities(@NonNull Date date, long structureId);

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