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

import com.muflone.android.django_hotels.database.models.Building;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface BuildingDao {
    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE extras = :extras " +
           "ORDER BY name")
    List<Building> listAll(boolean extras);

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE structure_id = :structureId " +
           "  AND extras = :extras")
    List<Building> listByStructure(long structureId, boolean extras);

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE id = :id")
    Building findById(long id);

    @Query("SELECT * " +
           "FROM buildings " +
           "WHERE name = :name")
    Building findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM buildings " +
           "WHERE extras = :extras")
    long count(boolean extras);

    @Insert(onConflict = IGNORE)
    long insert(Building item);

    @Insert(onConflict = IGNORE)
    void insert(Building... items);

    @Update
    void update(Building item);

    @Update
    void update(Building... items);

    @Delete
    void delete(Building item);

    @Delete
    void delete(Building... items);

    @Query("DELETE FROM buildings")
    void truncate();
}