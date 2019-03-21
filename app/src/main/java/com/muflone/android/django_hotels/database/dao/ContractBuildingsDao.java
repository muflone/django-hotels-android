package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.ContractBuildings;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface ContractBuildingsDao {
    @Query("SELECT * " +
           "FROM contract_buildings")
    List<ContractBuildings> getAll();

    @Query("SELECT * " +
           "FROM contract_buildings " +
           "WHERE contract_id = :contract_id")
    List<ContractBuildings> findByContract(long contract_id);

    @Query("SELECT * " +
           "FROM contract_buildings " +
           "WHERE building_id = :building_id")
    List<ContractBuildings> findByBuilding(long building_id);

    @Query("SELECT COUNT(*) " +
           "FROM contract_buildings")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(ContractBuildings item);

    @Insert(onConflict = IGNORE)
    void insert(ContractBuildings... items);

    @Delete
    void delete(ContractBuildings item);

    @Delete
    void delete(ContractBuildings... items);

    @Query("DELETE FROM contract_buildings")
    void truncate();
}