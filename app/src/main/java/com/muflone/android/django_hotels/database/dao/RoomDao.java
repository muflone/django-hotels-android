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

import com.muflone.android.django_hotels.database.models.Room;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface RoomDao {
    @Query("SELECT * " +
           "FROM rooms " +
           "ORDER BY name")
    List<Room> listAll();

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE building_id = :buildingId " +
           "ORDER BY name")
    List<Room> listByBuilding(long buildingId);

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE id = :id")
    Room findById(long id);

    @Query("SELECT * " +
           "FROM rooms " +
           "WHERE name = :name")
    Room findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM rooms")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Room item);

    @Insert(onConflict = IGNORE)
    void insert(Room... items);

    @Update
    void update(Room item);

    @Update
    void update(Room... items);

    @Delete
    void delete(Room item);

    @Delete
    void delete(Room... items);

    @Query("DELETE FROM rooms")
    void truncate();
}