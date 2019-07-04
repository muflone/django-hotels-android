package com.muflone.android.django_hotels.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
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
import com.muflone.android.django_hotels.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.tasks.AsyncTaskLoadDatabase;
import com.muflone.android.django_hotels.tasks.AsyncTaskResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void reload(Context context, AsyncTaskListener callback) {
        // Load data from database
        AsyncTaskLoadDatabase task = new AsyncTaskLoadDatabase(
                context,
                new AsyncTaskListener() {
            @Override
            public void onSuccess(AsyncTaskResult result) {
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

    public void backupDatabase(Context context, Settings settings) {
        // Backup database to external storage
        String destinationPath = String.format("backup_%s_%s.sqlite",
                this.getOpenHelper().getDatabaseName().replace(".sqlite", ""),
                new SimpleDateFormat("yyyy_MM_dd-HHmmss").format(new Date()));
        File sourceFile = new File(
                context.getApplicationInfo().dataDir +
                        File.separator +
                        "databases" +
                        File.separator +
                        this.getOpenHelper().getDatabaseName());
        File destinationDirectory = new File(
                Environment.getExternalStorageDirectory() +
                        File.separator +
                        settings.getPackageName() +
                        File.separator +
                        "backups");
        // Create missing destination directory
        if (! destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        File destinationFile = new File(
                destinationDirectory.getAbsoluteFile() +
                        File.separator +
                        destinationPath);
        try {
            FileInputStream inStream = new FileInputStream(sourceFile);
            FileOutputStream outStream = new FileOutputStream(destinationFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
