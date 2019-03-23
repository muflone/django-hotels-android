package com.muflone.android.django_hotels.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.dao.BuildingDao;
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
import com.muflone.android.django_hotels.database.dao.StructureDao;
import com.muflone.android.django_hotels.database.models.Brand;
import com.muflone.android.django_hotels.database.models.Building;
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
import com.muflone.android.django_hotels.database.models.Structure;

@Database(entities = {Brand.class, Building.class, Company.class,
                      Contract.class, ContractBuildings.class,
                      ContractType.class, Country.class, Employee.class, JobType.class,
                      Location.class, Region.class, Room.class, Structure.class},
          version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "hotels.sqlite";
    private static AppDatabase INSTANCE;
    public abstract BrandDao brandDao();
    public abstract BuildingDao buildingDao();
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
    public abstract StructureDao structureDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = android.arch.persistence.room.Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                // allow queries on the main thread.
                // Don’t do this on a real app! See PersistenceBasicSample for an example.
                //.allowMainThreadQueries()
                .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
        }
        INSTANCE = null;
    }
}