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

import com.muflone.android.django_hotels.database.models.CommandUsage;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface CommandUsageDao {
    @Query("SELECT * " +
           "FROM commands_usage " +
           "ORDER BY id")
    List<CommandUsage> listAll();

    @Query("SELECT * " +
           "FROM commands_usage " +
           "WHERE id = :id")
    CommandUsage findById(Long id);

    @Query("SELECT COUNT(*) " +
           "FROM commands_usage")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(CommandUsage item);

    @Insert(onConflict = IGNORE)
    void insert(CommandUsage... items);

    @Update
    void update(CommandUsage item);

    @Update
    void update(CommandUsage... items);

    @Delete
    void delete(CommandUsage item);

    @Delete
    void delete(CommandUsage... items);

    @Query("DELETE FROM commands_usage")
    void truncate();
}