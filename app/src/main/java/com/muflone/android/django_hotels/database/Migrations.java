package com.muflone.android.django_hotels.database;

import android.arch.persistence.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Migrations {
    static final MigrationSafe MIGRATION_5_TO_6 = new MigrationSafe(5, 6) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            // Define migrations actions
            List<String> actions = new ArrayList<>();
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
            actions.add("DROP TABLE IF EXISTS settings");
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
}
