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

package com.muflone.android.django_hotels.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;

@SuppressWarnings("WeakerAccess")
public abstract class MigrationSafe extends Migration {
    public MigrationSafe(int startVersion, int endVersion) {
        // Implements a more secure migration with database backup
        super(startVersion, endVersion);
    }

    public void migrate(@NonNull SupportSQLiteDatabase database, String[] actions) {
        // Execute a safe migration

        // Backup database to external storage
        this.backupDatabase(database);
        // Execute SQL actions
        for (String action : actions) {
            database.execSQL(action);
        }
        // Update database version
        this.updateVersion(database);
    }

    private void backupDatabase(@NonNull SupportSQLiteDatabase database) {
        // Backup database to external storage
        Singleton singleton = Singleton.getInstance();
        database.setTransactionSuccessful();
        database.endTransaction();
        Utility.backupDatabase(singleton.settings.context, database.getPath(), this.startVersion);
        database.beginTransaction();
    }

    private void updateVersion(@NonNull SupportSQLiteDatabase database) {
        // Update database version
        database.setVersion(this.endVersion);
    }
}
