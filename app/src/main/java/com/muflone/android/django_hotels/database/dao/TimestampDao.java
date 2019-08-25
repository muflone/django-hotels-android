/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
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

import com.muflone.android.django_hotels.database.models.ReportTimestamp;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface TimestampDao {
    @Query("SELECT * " +
           "FROM timestamps")
    List<Timestamp> listAll();

    @Query("SELECT DISTINCT " +
           "  timestamps.id, " +
           "  employees.first_name, " +
           "  employees.last_name, " +
           "  timestamps.datetime, " +
           "  timestamp_directions.description AS direction, " +
           "  timestamps.transmission AS transmission " +
           "FROM timestamps " +
           "INNER JOIN contracts " +
           "   ON contracts.id = timestamps.contract_id " +
           "INNER JOIN contract_buildings " +
           "   ON contract_buildings.contract_id = contracts.id " +
           "INNER JOIN buildings " +
           "   ON buildings.id = contract_buildings.building_id " +
           "INNER JOIN employees " +
           "   ON employees.id = contracts.employee_id " +
           "INNER JOIN timestamp_directions " +
           "   ON timestamp_directions.id = timestamps.direction_id " +
           "WHERE buildings.structure_id = :structureId " +
           "  AND timestamps.datetime BETWEEN :date AND :date + 86400000 - 1 " +
           "  AND (timestamp_directions.type_enter = 1 OR timestamp_directions.type_exit = 1) " +
           "ORDER BY timestamps.datetime DESC " +
           "LIMIT :count")
    List<TimestampEmployee> listByLatestEnterExit(Date date, long structureId, long count);

    @Query("SELECT * " +
           "FROM timestamps " +
           "WHERE contract_id = :contractId")
    List<Timestamp> listByName(long contractId);

    @Query("SELECT " +
           "  timestamps.* " +
           "FROM timestamps " +
           "INNER JOIN timestamp_directions " +
           "   ON timestamp_directions.id = timestamps.direction_id " +
           "WHERE contract_id = :contractId " +
           "  AND datetime = :date " +
           "  AND timestamp_directions.type_enter = 0 " +
           "  AND timestamp_directions.type_exit = 0")
    List<Timestamp> listByContractNotEnterExit(Date date, long contractId);

    @Query("SELECT * " +
           "FROM timestamps " +
           "WHERE transmission IS NULL " +
           "ORDER BY timestamps.datetime ASC")
    List<Timestamp> listByUntransmitted();

    @Query("SELECT DISTINCT " +
            "  employees.first_name, " +
            "  employees.last_name, " +
            "  timestamps.datetime, " +
            "  timestamp_directions.description AS direction " +
            "FROM timestamps " +
            "INNER JOIN contracts " +
            "   ON contracts.id = timestamps.contract_id " +
            "INNER JOIN contract_buildings " +
            "   ON contract_buildings.contract_id = contracts.id " +
            "INNER JOIN buildings " +
            "   ON buildings.id = contract_buildings.building_id " +
            "INNER JOIN employees " +
            "   ON employees.id = contracts.employee_id " +
            "INNER JOIN timestamp_directions " +
            "   ON timestamp_directions.id = timestamps.direction_id " +
            "WHERE buildings.structure_id = :structureId " +
            "  AND timestamps.datetime BETWEEN :date AND :date + 86400000 - 1 " +
            "ORDER BY employees.first_name ASC, " +
            "         employees.last_name ASC, " +
            "         timestamps.datetime ASC")
    List<ReportTimestamp> listForReportTimestamps(Date date, long structureId);

    @Query("SELECT * " +
           "FROM timestamps " +
           "WHERE id = :id")
    Timestamp findById(long id);

    @Query("SELECT COUNT(*) " +
           "FROM timestamps")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Timestamp item);

    @Insert(onConflict = IGNORE)
    void insert(Timestamp... items);

    @Update
    void update(Timestamp item);

    @Update
    void update(Timestamp... items);

    @Delete
    void delete(Timestamp item);

    @Delete
    void delete(Timestamp... items);

    @Query("DELETE FROM timestamps")
    void truncate();
}