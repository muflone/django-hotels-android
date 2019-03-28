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
    List<ContractBuildings> listAll();

    @Query("SELECT * " +
           "FROM contract_buildings " +
           "WHERE contract_id = :contract_id")
    List<ContractBuildings> listByContract(long contract_id);

    @Query("SELECT * " +
           "FROM contract_buildings " +
           "WHERE building_id = :building_id")
    List<ContractBuildings> listByBuilding(long building_id);

    @Query("SELECT contract_buildings.* " +
           "FROM contract_buildings " +
           "INNER JOIN contracts " +
           "   ON contracts.id = contract_buildings.contract_id " +
           "INNER JOIN employees " +
           "   ON employees.id = contracts.employee_id " +
           "INNER JOIN buildings " +
           "   ON buildings.id = contract_buildings.building_id " +
           "WHERE employees.id = :employee_id " +
           "ORDER BY buildings.name")
    List<ContractBuildings> listByEmployee(long employee_id);

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