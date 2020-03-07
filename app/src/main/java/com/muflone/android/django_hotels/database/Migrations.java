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

package com.muflone.android.django_hotels.database;

import android.arch.persistence.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Migrations {
    static final MigrationSafe MIGRATION_5_TO_6 = new MigrationSafe(5, 6) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            // Define migrations actions
            List<String> actions = new ArrayList<>();
            // Create table settings
            actions.add("CREATE TABLE IF NOT EXISTS settings (" +
                        "  name TEXT NOT NULL, " +
                        "  data TEXT, " +
                        "  PRIMARY KEY (name)" +
                        ")"
            );
            // Execute safe migration
            super.migrate(database, actions.toArray(new String[0]));
        }
    };

    static final MigrationSafe MIGRATION_6_TO_7 = new MigrationSafe(6, 7) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            // Define migrations actions
            List<String> actions = new ArrayList<>();
            // Drop table settings
            actions.add("DROP TABLE IF EXISTS settings");
            // Create table commands
            actions.add("CREATE TABLE IF NOT EXISTS commands (" +
                        "  id INTEGER, " +
                        "  name TEXT NOT NULL, " +
                        "  type TEXT NOT NULL, " +
                        "  context TEXT NOT NULL, " +
                        "  command TEXT, " +
                        "  uses INTEGER NOT NULL, " +
                        "  PRIMARY KEY (id)" +
                        ")"
            );
            actions.add("CREATE TABLE IF NOT EXISTS commands_usage (" +
                        "  id INTEGER, " +
                        "  used INTEGER NOT NULL, " +
                        "  PRIMARY KEY (id)" +
                        ")"
            );
            // Execute safe migration
            super.migrate(database, actions.toArray(new String[0]));
        }
    };

    static final MigrationSafe MIGRATION_7_TO_8 = new MigrationSafe(7, 8) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            // Define migrations actions
            List<String> actions = new ArrayList<>();
            // Add new column extras to table buildings
            actions.add("ALTER TABLE buildings " +
                        "ADD COLUMN extras INTEGER NOT NULL DEFAULT 0");
            // Delete any row from table activities with negative room_id (shouldn't exist)
            actions.add("DELETE FROM activities " +
                        "WHERE room_id < 0");
            // Add new column structure_id to table activities
            actions.add("ALTER TABLE activities " +
                        "ADD COLUMN structure_id INTEGER NOT NULL DEFAULT 0");
            // Update column structure using the structure from the room_id
            actions.add("UPDATE activities " +
                        "SET structure_id = COALESCE(( " +
                        "  SELECT buildings.structure_id " +
                        "  FROM rooms " +
                        "  LEFT JOIN buildings " +
                        "     ON buildings.id = rooms.building_id " +
                        "  WHERE rooms.id=activities.room_id " +
                        "), 0) " +
                        "WHERE activities.room_id > 0");
            // Add new column extras to table activities
            actions.add("ALTER TABLE activities " +
                    "ADD COLUMN extras INTEGER NOT NULL DEFAULT 0");
            // Execute safe migration
            super.migrate(database, actions.toArray(new String[0]));
        }
    };

    static final MigrationSafe MIGRATION_8_TO_9 = new MigrationSafe(8, 9) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            // Define migrations actions
            List<String> actions = new ArrayList<>();
            // Add new column structure_id to table timestamps
            actions.add("ALTER TABLE timestamps " +
                        "ADD COLUMN structure_id INTEGER NOT NULL DEFAULT 0");
            // Update column structure using the structure from the contract
            actions.add("UPDATE timestamps " +
                        "SET structure_id = COALESCE(( " +
                        "  SELECT buildings.structure_id " +
                        "  FROM contract_buildings" +
                        "  INNER JOIN buildings " +
                        "     ON buildings.id = contract_buildings.building_id " +
                        "  WHERE contract_buildings.contract_id = timestamps.contract_id " +
                        "), timestamps.structure_id) " +
                        "WHERE timestamps.structure_id = 0");
            // Execute safe migration
            super.migrate(database, actions.toArray(new String[0]));
        }
    };
}
