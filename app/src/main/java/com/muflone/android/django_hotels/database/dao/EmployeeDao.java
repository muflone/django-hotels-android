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