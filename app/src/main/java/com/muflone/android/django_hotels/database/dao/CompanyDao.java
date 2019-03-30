package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.Company;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface CompanyDao {
    @Query("SELECT * " +
           "FROM companies " +
           "ORDER BY name")
    List<Company> listAll();

    @Query("SELECT * " +
           "FROM companies " +
           "WHERE id = :id")
    Company findById(long id);

    @Query("SELECT * " +
           "FROM companies " +
           "WHERE name = :name")
    Company findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM companies")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Company item);

    @Insert(onConflict = IGNORE)
    void insert(Company... items);

    @Update
    void update(Company item);

    @Update
    void update(Company... items);

    @Delete
    void delete(Company item);

    @Delete
    void delete(Company... items);

    @Query("DELETE FROM companies")
    void truncate();
}