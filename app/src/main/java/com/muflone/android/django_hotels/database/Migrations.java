package com.muflone.android.django_hotels.database;

import android.arch.persistence.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

public class Migrations {
    static final MigrationSafe MIGRATION_5_TO_6 = new MigrationSafe(5, 6) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            super.migrate(database);
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS settings (" +
                    "  name TEXT NOT NULL, " +
                    "  data TEXT, " +
                    "  PRIMARY KEY (name)" +
                    ")");
            this.updateVersion(database);
        }
    };

    static final MigrationSafe MIGRATION_6_TO_7 = new MigrationSafe(6, 7) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            super.migrate(database);
            database.execSQL(
                    "DROP TABLE IF EXISTS settings"
            );
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS commands (" +
                    "  id INTEGER, " +
                    "  name TEXT NOT NULL, " +
                    "  type TEXT NOT NULL, " +
                    "  context TEXT NOT NULL, " +
                    "  command TEXT, " +
                    "  uses INTEGER NOT NULL, " +
                    "  PRIMARY KEY (id)" +
                    ")");
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS commands_usage (" +
                    "  id INTEGER, " +
                    "  used INTEGER NOT NULL, " +
                    "  PRIMARY KEY (id)" +
                    ")"
            );
            this.updateVersion(database);
        }
    };
}
