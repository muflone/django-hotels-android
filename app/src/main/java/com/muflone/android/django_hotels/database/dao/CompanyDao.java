package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Company;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface CompanyDao {
    @Query("SELECT * FROM companies")
    List<Company> getAll();

    @Query("SELECT * FROM companies WHERE id = :id")
    Company findById(int id);

    @Query("SELECT * FROM companies WHERE name = :name")
    Company findByName(String name);

    @Query("SELECT COUNT(*) FROM companies")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Company item);

    @Insert(onConflict = IGNORE)
    void insert(Company... items);

    @Delete
    void delete(Company item);

    @Delete
    void delete(Company... items);

    @Query("DELETE FROM companies")
    void truncate();
}