/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.muflone.android.django_hotels.database.models.ContractBuildings;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
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

    @Update
    void update(ContractBuildings item);

    @Update
    void update(ContractBuildings... items);

    @Delete
    void delete(ContractBuildings item);

    @Delete
    void delete(ContractBuildings... items);

    @Query("DELETE FROM contract_buildings")
    void truncate();
}