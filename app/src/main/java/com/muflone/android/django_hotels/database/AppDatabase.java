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

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.database.Cursor;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.dao.BuildingDao;
import com.muflone.android.django_hotels.database.dao.CommandDao;
import com.muflone.android.django_hotels.database.dao.CommandUsageDao;
import com.muflone.android.django_hotels.database.dao.CompanyDao;
import com.muflone.android.django_hotels.database.dao.ContractBuildingsDao;
import com.muflone.android.django_hotels.database.dao.ContractDao;
import com.muflone.android.django_hotels.database.dao.ContractTypeDao;
import com.muflone.android.django_hotels.database.dao.CountryDao;
import com.muflone.android.django_hotels.database.dao.EmployeeDao;
import com.muflone.android.django_hotels.database.dao.JobTypeDao;
import com.muflone.android.django_hotels.database.dao.LocationDao;
import com.muflone.android.django_hotels.database.dao.RegionDao;
import com.muflone.android.django_hotels.database.dao.RoomDao;
import com.muflone.android.django_hotels.database.dao.ServiceActivityDao;
import com.muflone.android.django_hotels.database.dao.ServiceDao;
import com.muflone.android.django_hotels.database.dao.StructureDao;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.dao.TimestampDirectionDao;
import com.muflone.android.django_hotels.database.models.Brand;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;
import com.muflone.android.django_hotels.database.models.Company;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Country;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.JobType;
import com.muflone.android.django_hotels.database.models.Location;
import com.muflone.android.django_hotels.database.models.Region;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.tasks.TaskListenerInterface;
import com.muflone.android.django_hotels.tasks.TaskLoadDatabase;
import com.muflone.android.django_hotels.tasks.TaskResult;

import java.io.File;

@Database(entities = {Brand.class, Building.class, Command.class, CommandUsage.class,
                      Company.class, Contract.class, ContractBuildings.class,
                      ContractType.class, Country.class, Employee.class, JobType.class,
                      Location.class, Region.class, Room.class, Service.class,
                      ServiceActivity.class, Structure.class, Timestamp.class,
                      TimestampDirection.class},
          version = Constants.DATABASE_VERSION)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract BrandDao brandDao();
    public abstract BuildingDao buildingDao();
    public abstract CommandDao commandDao();
    public abstract CommandUsageDao commandUsageDao();
    public abstract CompanyDao companyDao();
    public abstract ContractDao contractDao();
    public abstract ContractBuildingsDao contractBuildingsDao();
    public abstract ContractTypeDao contractTypeDao();
    public abstract CountryDao countryDao();
    public abstract EmployeeDao employeeDao();
    public abstract JobTypeDao jobTypeDao();
    public abstract LocationDao locationDao();
    public abstract RegionDao regionDao();
    public abstract RoomDao roomDao();
    public abstract ServiceActivityDao serviceActivityDao();
    public abstract ServiceDao serviceDao();
    public abstract StructureDao structureDao();
    public abstract TimestampDao timestampDao();
    public abstract TimestampDirectionDao timestampDirectionDao();

    public static synchronized AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = android.arch.persistence.room.Room.databaseBuilder(
                    context, AppDatabase.class, Constants.DATABASE_NAME)
                // Allow schema changes even without any migration
                //.fallbackToDestructiveMigration()
                // Execute schema migrations to save the user data
                .addMigrations(Migrations.MIGRATION_5_TO_6)
                .addMigrations(Migrations.MIGRATION_6_TO_7)
                .addMigrations(Migrations.MIGRATION_7_TO_8)
                .addMigrations(Migrations.MIGRATION_8_TO_9)
                // allow queries on the main thread.
                // Don’t do this on a real app! See PersistenceBasicSample for an example.
                //.allowMainThreadQueries()
                .build();
        }
        return INSTANCE;
    }

    public synchronized void destroyInstance() {
        if (INSTANCE != null) {
            // Execute checkpoint only for valid database status
            if (INSTANCE.checkDB()) {
                INSTANCE.checkpoint();
            }
            INSTANCE.close();
        }
        INSTANCE = null;
    }

    public boolean checkDB() {
        try {
            return INSTANCE.companyDao().count() >= 0;
        } catch (IllegalStateException exception) {
            // The database state is invalid
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private synchronized int checkpoint() {
        Cursor cursor = INSTANCE.query("PRAGMA wal_checkpoint(truncate)", null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    public void reload(Context context, TaskListenerInterface callback) {
        // Load data from database
        TaskLoadDatabase task = new TaskLoadDatabase(
                context,
                new TaskListenerInterface() {
            @Override
            public void onSuccess(TaskResult result) {
                Singleton.getInstance().apiData = result.data;
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                if (callback != null) {
                    callback.onFailure(exception);
                }
            }

            @Override
            public void onProgress(int step, int total) {
                if (callback != null) {
                    callback.onProgress(step, total);
                }
            }
        });
        task.execute();
    }

    @SuppressWarnings("UnusedReturnValue")
    public String backupDatabase(Context context) {
        // Backup database to external storage
        String databasePath = context.getApplicationInfo().dataDir +
                File.separator +
                "databases" +
                File.separator +
                this.getOpenHelper().getDatabaseName();
        return Utility.backupDatabase(context, databasePath, Constants.DATABASE_VERSION);
    }
}
