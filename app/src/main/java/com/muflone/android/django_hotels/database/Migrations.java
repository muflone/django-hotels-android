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
            // Execute safe migration
            super.migrate(database, actions.toArray(new String[0]));
        }
    };
}
