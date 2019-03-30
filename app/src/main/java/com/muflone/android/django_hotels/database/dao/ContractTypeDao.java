package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.ContractType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface ContractTypeDao {
    @Query("SELECT * " +
           "FROM contract_types")
    List<ContractType> listAll();

    @Query("SELECT * " +
           "FROM contract_types " +
           "WHERE id = :id")
    ContractType findById(long id);

    @Query("SELECT * " +
           "FROM contract_types " +
           "WHERE name = :name")
    ContractType findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM contract_types")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(ContractType item);

    @Insert(onConflict = IGNORE)
    void insert(ContractType... items);

    @Update
    void update(ContractType item);

    @Delete
    void delete(ContractType item);

    @Delete
    void delete(ContractType... items);

    @Query("DELETE FROM contract_types")
    void truncate();
}