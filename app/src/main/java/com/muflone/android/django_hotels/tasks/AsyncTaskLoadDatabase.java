package com.muflone.android.django_hotels.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.dao.BuildingDao;
import com.muflone.android.django_hotels.database.dao.CommandDao;
import com.muflone.android.django_hotels.database.dao.CommandTypeDao;
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
import com.muflone.android.django_hotels.database.dao.ServiceDao;
import com.muflone.android.django_hotels.database.dao.StructureDao;
import com.muflone.android.django_hotels.database.dao.TabletSettingDao;
import com.muflone.android.django_hotels.database.dao.TimestampDirectionDao;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandType;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.TabletSetting;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AsyncTaskLoadDatabase extends AsyncTask<Void, Void, AsyncTaskResult> {
    private final AsyncTaskListener callback;
    private final WeakReference<Context> context;

    public AsyncTaskLoadDatabase(Context context, AsyncTaskListener callback) {
        this.context = new WeakReference<>(context);
        this.callback = callback;
    }

    @Override
    protected AsyncTaskResult doInBackground(Void... params) {
        ApiData data = new ApiData();
        AppDatabase database = AppDatabase.getAppDatabase(this.context.get());
        // TODO: Database deletion is dangerous, must implement migrations
        if (!database.checkDB()) {
            Log.d("", "Invalid database structure " + database.getOpenHelper().getDatabaseName());
            AppDatabase.destroyInstance();
            File databaseFile = new File(this.context.get().getApplicationInfo().dataDir +
                    "/databases/" + database.getOpenHelper().getDatabaseName());
            //noinspection ResultOfMethodCallIgnored
            databaseFile.delete();
            database = AppDatabase.getAppDatabase(this.context.get());
        }
        BrandDao brandDao = database.brandDao();
        BuildingDao buildingDao = database.buildingDao();
        CommandDao commandDao = database.commandDao();
        CommandTypeDao commandTypeDao = database.commandTypeDao();
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
        ServiceDao serviceDao = database.serviceDao();
        StructureDao structureDao = database.structureDao();
        TabletSettingDao tabletSettingDao = database.tabletSettingDao();
        TimestampDirectionDao timestampDirectionDao = database.timestampDirectionDao();
        // Load Structures
        for(Structure structure : structureDao.listAll()) {
            data.structuresMap.put(structure.id, structure);
            structure.company = companyDao.findById(structure.companyId);
            structure.brand = brandDao.findById(structure.brandId);
            data.brandsMap.put(structure.brand.id, structure.brand);
            // Load Structure location
            structure.location = locationDao.findById(structure.locationId);
            structure.location.region = regionDao.findById(structure.location.regionId);
            structure.location.country = countryDao.findById(structure.location.countryId);
            // Load buildings
            structure.buildings = buildingDao.listByStructure(structure.id);
            // Load employees
            structure.employees = new ArrayList<>();
            for (Employee employee : employeeDao.listByStructure(structure.id)) {
                employee.contractBuildings = contractBuildingsDao.listByEmployee(employee.id);
                structure.employees.add(employee);
            }
            for(Building building : structure.buildings) {
                data.buildingsMap.put(building.id, building);
                // Load building location
                building.location = locationDao.findById(building.locationId);
                building.location.region = regionDao.findById(building.location.regionId);
                building.location.country = countryDao.findById(building.location.countryId);
                // Load rooms
                building.rooms = roomDao.listByBuilding(building.id);
                for (Room room : building.rooms) {
                    data.roomsMap.put(room.id, room);
                    data.roomsStructureMap.put(room.id, structure);
                    data.roomsBuildingMap.put(room.id, building);
                }
                // Load employees
                building.employees = new ArrayList<>();
                for (Employee employee : employeeDao.listByBuilding(building.id)) {
                    employee.contractBuildings = contractBuildingsDao.listByEmployee(employee.id);
                    building.employees.add(employee);
                }
            }
        }
        // Load Contracts
        for(Contract contract : contractDao.listAll()) {
            data.contractsMap.put(contract.id, contract);
            data.contractsGuidMap.put(contract.guid, contract);
            contract.employee = employeeDao.findById(contract.employeeId);
            data.employeesMap.put(contract.employee.id, contract.employee);
            contract.employee.contractBuildings = contractBuildingsDao.listByEmployee(contract.employeeId);
            contract.company = companyDao.findById(contract.companyId);
            data.companiesMap.put(contract.company.id, contract.company);
            contract.contractType = contractTypeDao.findById(contract.contractTypeId);
            data.contractTypeMap.put(contract.contractType.id, contract.contractType);
            contract.jobType = jobTypeDao.findById(contract.jobTypeId);
            data.jobTypesMap.put(contract.jobType.id, contract.jobType);
            data.contractsMap.put(contract.id, contract);
        }
        // Load Services
        for(Service service : serviceDao.listAll()) {
            if (service.extra_service) {
                data.serviceExtraMap.put(service.id, service);
            } else {
                data.serviceMap.put(service.id, service);
            }
        }
        // Load Timestamp directions
        for(TimestampDirection timestampDirection : timestampDirectionDao.listAll()) {
            data.timestampDirectionsMap.put(timestampDirection.id, timestampDirection);
        }
        data.enterDirection = timestampDirectionDao.findByTypeEnter();
        data.exitDirection = timestampDirectionDao.findByTypeExit();
        data.timestampDirectionsNotEnterExit = timestampDirectionDao.listNotEnterExit();
        // Load TabletSettings
        for(TabletSetting tabletSetting : tabletSettingDao.listAll()) {
            data.tabletSettingsMap.put(tabletSetting.name, tabletSetting);
        }
        // Load Command Types
        for(CommandType commandType : commandTypeDao.listAll()) {
            data.commandTypesMap.put(commandType.id, commandType);
        }
        // Load Commands
        for(Command command : commandDao.listAll()) {
            command.type = data.commandTypesMap.get(command.typeId);
            data.commandsMap.put(command.id, command);
        }
        return new AsyncTaskResult(data, data.exception);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult result) {
        super.onPostExecute(result);
        // Check if callback listener was requested
        if (this.callback != null & result != null) {
            if (result.exception == null) {
                // Return flow to the caller
                this.callback.onSuccess(result);
            } else {
                // Failure with exception
                this.callback.onFailure(result.exception);
            }
        }
    }
}
