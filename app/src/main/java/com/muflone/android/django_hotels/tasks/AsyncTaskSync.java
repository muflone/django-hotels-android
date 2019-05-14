package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
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
import com.muflone.android.django_hotels.database.dao.ServiceDao;
import com.muflone.android.django_hotels.database.dao.StructureDao;
import com.muflone.android.django_hotels.database.dao.TabletSettingDao;
import com.muflone.android.django_hotels.database.dao.TimestampDirectionDao;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.TabletSetting;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AsyncTaskSync extends AsyncTask<Void, Void, AsyncTaskResult> {
    private final String TAG = getClass().getName();

    private final Api api;
    private final AsyncTaskListener callback;
    private final AppDatabase database;
    public final static int totalSteps = 4;
    private int currentStep;

    public AsyncTaskSync(Api api, AppDatabase database, AsyncTaskListener callback) {
        this.api = api;
        this.callback = callback;
        this.database = database;
        this.currentStep = 0;
    }

    @Override
    protected AsyncTaskResult doInBackground(Void... params) {
        // Do the background job
        boolean transmissionErrors = false;

        // Check if the system date/time matches with the remote date/time
        ApiData data = this.api.checkDates();
        if (data.exception == null) {
            this.updateProgress();
            // Transmit any incomplete timestamp (UPLOAD)
            List<Timestamp> timestampsList = this.database.timestampDao().listByUntrasmitted();
            for (Timestamp timestamp : timestampsList) {
                data = this.putTimestamp(timestamp);
                if (data.exception == null) {
                    // Update transmission date
                    timestamp.transmission = this.api.getCurrentDateTime();
                    this.database.timestampDao().update(timestamp);
                } else {
                    // There were some errors during the timestamps transmissions
                    transmissionErrors = true;
                }
            }
            this.updateProgress();
            // Transmit any incomplete activity (UPLOAD)
            List<ServiceActivity> servicesActivityList = this.database.serviceActivityDao().listByUntrasmitted();
            for (ServiceActivity serviceActivity : servicesActivityList) {
                data = this.putActivity(serviceActivity);
                if (data.exception == null) {
                    serviceActivity.transmission = this.api.getCurrentDateTime();
                    this.database.serviceActivityDao().update(serviceActivity);
                } else {
                    // There were some errors during the timestamps transmissions
                    transmissionErrors = true;
                }
            }
            this.updateProgress();
            // If no errors were given during the upload proceed to the download
            if (! transmissionErrors) {
                // Get new data from the server (DOWNLOAD)
                data = this.downloadData();
                if (data.exception == null) {
                    // Success, save data in database
                    this.saveToDatabase(data);
                }
            }
            this.updateProgress();
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

    private ApiData downloadData() {
        ApiData result = new ApiData();
        // Get data from the server
        JSONObject jsonRoot = this.api.getJSONObject(String.format("get/%s/%s/",
                this.api.settings.getTabletID(),
                this.api.getCurrentTokenCode()));
        if (jsonRoot != null) {
            try {
                // Loop over every structure
                JSONObject jsonStructures = jsonRoot.getJSONObject("structures");
                Iterator<?> jsonKeys = jsonStructures.keys();
                while (jsonKeys.hasNext()) {
                    String key = (String) jsonKeys.next();
                    Structure objStructure = new Structure(jsonStructures.getJSONObject(key));
                    result.structuresMap.put(objStructure.id, objStructure);
                }
                // Loop over every contract
                JSONArray jsonContracts = jsonRoot.getJSONArray("contracts");
                for (int i = 0; i < jsonContracts.length(); i++) {
                    Contract contract = new Contract(jsonContracts.getJSONObject(i));
                    result.contractsMap.put(contract.id, contract);
                }
                // Loop over every service
                JSONArray jsonServices = jsonRoot.getJSONArray("services");
                for (int i = 0; i < jsonServices.length(); i++) {
                    Service service = new Service(jsonServices.getJSONObject(i));
                    if (service.extra_service) {
                        result.serviceExtraMap.put(service.id, service);
                    } else {
                        result.serviceMap.put(service.id, service);
                    }
                }
                // Loop over every timestamp direction
                JSONArray jsonTimestampDirections = jsonRoot.getJSONArray("timestamp_directions");
                for (int i = 0; i < jsonTimestampDirections.length(); i++) {
                    TimestampDirection timestampDirection = new TimestampDirection(jsonTimestampDirections.getJSONObject(i));
                    result.timestampDirectionsMap.put(timestampDirection.id, timestampDirection);
                }
                // Loop over every tablet settings
                JSONArray jsonSettings = jsonRoot.getJSONArray("settings");
                for (int i = 0; i < jsonSettings.length(); i++) {
                    TabletSetting tabletSetting = new TabletSetting(jsonSettings.getJSONObject(i));
                    result.tabletSettingsMap.put(tabletSetting.name, tabletSetting);
                }
                // Check the final node for successful reads
                this.api.checkStatusResponse(jsonRoot);
            } catch (JSONException e) {
                result.exception = new InvalidResponseException();
            } catch (ParseException e) {
                result.exception = new InvalidResponseException();
            } catch (InvalidResponseException e) {
                result.exception = e;
            }
        } else {
            // Unable to download data from the server
            result.exception = new NoDownloadException();
        }
        return result;
    }

    private ApiData putTimestamp(Timestamp timestamp) {
        ApiData result = new ApiData();
        JSONObject jsonRoot = null;
        // Send timestamps to the server
        try {
            jsonRoot = this.api.getJSONObject(String.format(Locale.ROOT,
                    "put/timestamp/%s/%s/%d/%d/%d/%s/",
                    this.api.settings.getTabletID(),
                    this.api.getCurrentTokenCode(),
                    timestamp.contractId,
                    timestamp.directionId,
                    timestamp.datetime.getTime() / 1000,
                    URLEncoder.encode(timestamp.description.replace("\n", "\\n"), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            result.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (status.equals(Api.STATUS_EXISTING)) {
                    Log.w(TAG, String.format("Existing timestamp during the data transmission: %s", status));
                } else if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException e) {
                result.exception = new InvalidResponseException();
            } catch (InvalidResponseException e) {
                result.exception = e;
            }
        } else {
            // Unable to download data from the server
            result.exception = new NoDownloadException();
        }
        return result;
    }

    private ApiData putActivity(ServiceActivity serviceActivity) {
        ApiData result = new ApiData();
        JSONObject jsonRoot = null;
        // Send timestamps to the server
        try {
            jsonRoot = this.api.getJSONObject(String.format(Locale.ROOT,
                    "put/activity/%s/%s/%d/%d/%d/%d/%d/%s/",
                    this.api.settings.getTabletID(),
                    this.api.getCurrentTokenCode(),
                    serviceActivity.contractId,
                    serviceActivity.roomId,
                    serviceActivity.serviceId,
                    serviceActivity.serviceQty,
                    serviceActivity.date.getTime() / 1000,
                    URLEncoder.encode(serviceActivity.description.replace("\n", "\\n") , "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            result.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (status.equals(Api.STATUS_EXISTING)) {
                    Log.w(TAG, String.format("Existing activity during the data transmission: %s", status));
                } else if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException e) {
                result.exception = new InvalidResponseException();
            } catch (InvalidResponseException e) {
                result.exception = e;
            }
        } else {
            // Unable to download data from the server
            result.exception = new NoDownloadException();
        }
        return result;
    }

    private void saveToDatabase(ApiData data) {
        BrandDao brandDao = this.database.brandDao();
        BuildingDao buildingDao = this.database.buildingDao();
        CompanyDao companyDao = this.database.companyDao();
        ContractDao contractDao = this.database.contractDao();
        ContractBuildingsDao contractBuildingsDao = this.database.contractBuildingsDao();
        ContractTypeDao contractTypeDao = this.database.contractTypeDao();
        JobTypeDao jobTypeDao = this.database.jobTypeDao();
        CountryDao countryDao = this.database.countryDao();
        EmployeeDao employeeDao = this.database.employeeDao();
        LocationDao locationDao = this.database.locationDao();
        RegionDao regionDao = this.database.regionDao();
        RoomDao roomDao = this.database.roomDao();
        ServiceDao serviceDao = this.database.serviceDao();
        StructureDao structureDao = this.database.structureDao();
        TabletSettingDao tabletSettingDao = this.database.tabletSettingDao();
        TimestampDirectionDao timestampDirectionDao = this.database.timestampDirectionDao();

        // Delete previous data
        tabletSettingDao.truncate();
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
        serviceDao.truncate();
        timestampDirectionDao.truncate();

        // Save data from structures
        for (Structure structure : data.structuresMap.values()) {
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
        for (Contract contract : data.contractsMap.values()) {
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
        // Save data for services
        for (Service service : data.serviceMap.values()) {
            serviceDao.insert(service);
        }
        // Save data for timestamp directions
        for (TimestampDirection timestampDirection : data.timestampDirectionsMap.values()) {
            timestampDirectionDao.insert(timestampDirection);
        }
        // Save data for tablet settings
        for (TabletSetting tabletSetting : data.tabletSettingsMap.values()) {
            tabletSettingDao.insert(tabletSetting);
        }
    }

    private void updateProgress() {
        this.currentStep += 1;
        if (this.callback != null) {
            this.callback.onProgress(this.currentStep, AsyncTaskSync.totalSteps);
        }
    }
}
