package com.muflone.android.django_hotels.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;

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
