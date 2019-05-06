package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Employee;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface EmployeeDao {
    @Query("SELECT * " +
           "FROM employees " +
           "ORDER BY first_name, last_name")
    List<Employee> listAll();

    @Query("SELECT DISTINCT employees.* " +
           "FROM employees " +
           "INNER JOIN contracts " +
           "   ON contracts.employee_id = employees.id " +
           "INNER JOIN contract_buildings " +
           "   ON contract_buildings.contract_id = contracts.id " +
           "WHERE contract_buildings.building_id = :building_id")
    List<Employee> listByBuilding(long building_id);

    @Query("SELECT DISTINCT employees.* " +
           "FROM employees " +
           "INNER JOIN contracts " +
           "   ON contracts.employee_id = employees.id " +
           "INNER JOIN contract_buildings " +
           "   ON contract_buildings.contract_id = contracts.id " +
           "INNER JOIN buildings " +
           "   ON buildings.id = contract_buildings.building_id " +
           "INNER JOIN structures " +
           "   ON structures.id = buildings.structure_id " +
           "WHERE structures.id = :structure_id " +
           "ORDER BY first_name, last_name")
    List<Employee> listByStructure(long structure_id);

    @Query("SELECT * " +
           "FROM employees " +
           "WHERE id = :id")
    Employee findById(long id);

    @Query("SELECT * " +
           "FROM employees " +
           "WHERE first_name = :first_name " +
           "  AND last_name = :last_name")
    Employee findByName(String first_name, String last_name);

    @Query("SELECT COUNT(*) " +
           "FROM employees")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Employee item);

    @Insert(onConflict = IGNORE)
    void insert(Employee... items);

    @Update
    void update(Employee item);

    @Update
    void update(Employee... items);

    @Delete
    void delete(Employee item);

    @Delete
    void delete(Employee... items);

    @Query("DELETE FROM employees")
    void truncate();
}