package com.muflone.android.django_hotels.api.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
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
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.ArrayList;

public class AsyncTaskLoadDatabase extends AsyncTask<Void, Void, ApiData> {
    private final Api api;
    private final AsyncTaskListener callback;

    public AsyncTaskLoadDatabase(Api api, AsyncTaskListener callback) {
        this.api = api;
        this.callback = callback;
    }

    @Override
    protected ApiData doInBackground(Void... params) {
        ApiData data = new ApiData();
        AppDatabase database = AppDatabase.getAppDatabase(api.context);
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
        // Load Structures
        for(Structure structure : structureDao.getAll()) {
            structure.company = companyDao.findById(structure.companyId);
            structure.brand = brandDao.findById(structure.brandId);
            // Load Structure location
            structure.location = locationDao.findById(structure.locationId);
            structure.location.region = regionDao.findById(structure.location.regionId);
            structure.location.country = countryDao.findById(structure.location.countryId);
            // Load buildings
            structure.buildings = buildingDao.listByStructure(structure.id);
            // Load employees
            structure.employees = new ArrayList<Employee>();
            for (Employee employee : employeeDao.findByStructure(structure.id)) {
                employee.contractBuildings = contractBuildingsDao.findByEmployee(employee.id);
                structure.employees.add(employee);
            }
            for(Building building : structure.buildings) {
                // Load building location
                building.location = locationDao.findById(building.locationId);
                building.location.region = regionDao.findById(building.location.regionId);
                building.location.country = countryDao.findById(building.location.countryId);
                // Load rooms
                building.rooms = roomDao.listByBuilding(building.id);
                // Load employees
                building.employees = new ArrayList<Employee>();
                for (Employee employee : employeeDao.findByBuilding(building.id)) {
                    building.employees.add(employee);
                }
            }
            data.structures.add(structure);
        }
        // Load Contracts
        for(Contract contract : contractDao.getAll()) {
            contract.employee = employeeDao.findById(contract.employeeId);
            contract.employee.contractBuildings = contractBuildingsDao.findByEmployee(contract.employeeId);
            contract.company = companyDao.findById(contract.companyId);
            contract.contractType = contractTypeDao.findById(contract.contractTypeId);
            contract.jobType = jobTypeDao.findById(contract.jobTypeId);
            data.contracts.add(contract);
        }
        return data;
    }

    @Override
    protected void onPostExecute(ApiData data) {
        super.onPostExecute(data);
        // Check if callback listener was requested
        if (this.callback != null & data != null) {
            if (data.exception == null) {
                // Return flow to the caller
                this.callback.onSuccess(data);
            } else {
                // Failure with exception
                this.callback.onFailure(data.exception);
            }
        }
    }
}
