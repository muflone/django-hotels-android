package com.muflone.android.django_hotels.api.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.GetDataResults;
import com.muflone.android.django_hotels.database.AppDatabase;
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
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Structure;

public class AsyncTaskDownload extends AsyncTask<Void, Void, GetDataResults> {
    private final Api api;
    private final AsyncTaskListener callback;

    public AsyncTaskDownload(Api api, AsyncTaskListener callback) {
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected GetDataResults doInBackground(Void... params) {
        // Do the background job
        GetDataResults results = this.api.getData(this.api.settings.getTabletID(),
                this.api.getCurrentTokenCode());
        if (results.exception == null) {
            // Success, save data in database
            this.saveToDatabase(results, this.api.context);
        }
        return results;
    }

    @Override
    protected void onPostExecute(GetDataResults results) {
        super.onPostExecute(results);
        // Check if callback listener was requested
        if (this.callback != null & results != null) {
            if (results.exception == null) {
                // Return flow to the caller
                this.callback.onSuccess(results);
            } else {
                // Failure with exception
                this.callback.onFailure(results.exception);
            }
        }
    }

    private void saveToDatabase(GetDataResults results, Context context) {
        AppDatabase database = AppDatabase.getAppDatabase(context);
        // Save brands
        BrandDao brandDao = database.brandDao();
        BuildingDao buildingDao = database.buildingDao();
        CompanyDao companyDao = database.companyDao();
        ContractDao contractDao = database.contractDao();
        ContractBuildingsDao contractBuildingsDao = database.contractBuildingsDao();
        ContractTypeDao contractTypeDao = database.contractTypeDao();
        JobTypeDao jobTypeDao = database.jobTypeDao();
        CountryDao countryDao = database.countryDao();
        EmployeeDao employeeDao = database.employeeDao();
        LocationDao locationDao = database.locationDao();
        RegionDao regionDao = database.regionDao();
        RoomDao roomDao = database.roomDao();
        StructureDao structureDao = database.structureDao();

        // Delete previous data
        roomDao.truncate();
        contractBuildingsDao.truncate();
        buildingDao.truncate();
        contractDao.truncate();
        contractTypeDao.truncate();
        jobTypeDao.truncate();
        structureDao.truncate();
        locationDao.truncate();
        regionDao.truncate();
        countryDao.truncate();
        companyDao.truncate();
        brandDao.truncate();

        // Save data from structures
        for (Structure structure : results.structures) {
            brandDao.insert(structure.brand);
            companyDao.insert(structure.company);
            countryDao.insert(structure.location.country);
            regionDao.insert(structure.location.region);
            locationDao.insert(structure.location);
            structureDao.insert(structure);
            // Save buildings
            for (Building building : structure.buildings) {
                countryDao.insert(building.location.country);
                regionDao.insert(building.location.region);
                locationDao.insert(building.location);
                buildingDao.insert(building);
                // Save rooms
                for (Room room : building.rooms) {
                    roomDao.insert(room);
                }
            }
        }
        // Save data from contracts
        for (Contract contract : results.contracts) {
            employeeDao.insert(contract.employee);
            companyDao.insert(contract.company);
            contractTypeDao.insert(contract.contractType);
            jobTypeDao.insert(contract.jobType);
            contractDao.insert(contract);
            // Save ContractBuildings
            for (long building_id : contract.buildings) {
                contractBuildingsDao.insert(new ContractBuildings(contract.id, building_id));
            }
        }
        return;
    }
}
