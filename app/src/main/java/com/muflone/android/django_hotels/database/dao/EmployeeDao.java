package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Employee;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface EmployeeDao {
    @Query("SELECT * " +
           "FROM employees")
    List<Employee> getAll();

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

    @Delete
    void delete(Employee item);

    @Delete
    void delete(Employee... items);

    @Query("DELETE FROM employees")
    void truncate();
}