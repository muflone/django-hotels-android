package com.muflone.android.django_hotels.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.InvalidServerStatusException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.api.exceptions.RetransmittedActivityException;
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
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AsyncTaskSync extends AsyncTask<Void, Void, AsyncTaskResult> {
    private final String TAG = getClass().getName();

    private final Api api;
    private final AsyncTaskListener callback;
    private final AppDatabase database;
    private final int totalSteps;
    private final Singleton singleton = Singleton.getInstance();
    private final WeakReference<Context> context;
    private int currentStep;

    public AsyncTaskSync(Context context, Api api, AppDatabase database, int totalSteps, AsyncTaskListener callback) {
        this.context = new WeakReference<>(context);
        this.api = api;
        this.callback = callback;
        this.database = database;
        this.currentStep = 0;
        this.totalSteps = totalSteps;
    }

    @Override
    protected AsyncTaskResult doInBackground(Void... params) {
        // Do the background job
        boolean transmissionErrors = false;
        ApiData data;

        // Check the server status
        this.updateProgress();
        data = this.checkStatus();
        if (data.exception == null) {
            // Check if the system date/time matches with the remote date/time
            this.updateProgress();
            data = this.checkDates();
            if (data.exception == null) {
                // Transmit any incomplete timestamp (UPLOAD)
                this.updateProgress();
                List<Timestamp> timestampsList = this.database.timestampDao().listByUntrasmitted();
                for (Timestamp timestamp : timestampsList) {
                    data = this.putTimestamp(timestamp);
                    if (data.exception == null) {
                        // Update transmission date
                        timestamp.transmission = Utility.getCurrentDateTime();
                        this.database.timestampDao().update(timestamp);
                    } else {
                        // There were some errors during the timestamps transmissions
                        transmissionErrors = true;
                    }
                }
                // Transmit any incomplete activity (UPLOAD)
                this.updateProgress();
                List<ServiceActivity> servicesActivityList = this.database.serviceActivityDao().listByUntrasmitted();
                for (ServiceActivity serviceActivity : servicesActivityList) {
                    data = this.putActivity(serviceActivity);
                    if (data.exception == null) {
                        serviceActivity.transmission = Utility.getCurrentDateTime();
                        this.database.serviceActivityDao().update(serviceActivity);
                    } else {
                        // There were some errors during the timestamps transmissions
                        transmissionErrors = true;
                    }
                }
                // If no errors were given during the upload proceed to the download
                if (!transmissionErrors) {
                    // Get new data from the server (DOWNLOAD)
                    this.updateProgress();
                    data = this.downloadData();
                    if (data.exception == null) {
                        // Success, save data in database
                        this.updateProgress();
                        this.saveToDatabase(data);
                        // Synchronization complete
                        this.updateProgress();
                    }
                }
            }
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

    private void checkStatusResponse(JSONObject jsonObject) throws InvalidResponseException, InvalidServerStatusException {
        // Check the status object for valid data
        try {
            if (!jsonObject.getString("status").equals(Api.STATUS_OK)) {
                throw new InvalidServerStatusException();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidResponseException();
        }
    }

    private ApiData checkStatus() {
        // Check if the server status
        ApiData data = new ApiData();
        JSONObject jsonRoot = this.api.getJSONObject(String.format("status/%s/",
                this.api.settings.getTabletID()));
        if (jsonRoot != null) {
            try {
                // Check the status node for successful reads
                this.checkStatusResponse(jsonRoot);
            } catch (InvalidResponseException exception) {
                exception.printStackTrace();
                data.exception = exception;
            } catch (InvalidServerStatusException exception) {
                exception.printStackTrace();
                data.exception = exception;
            }
        } else {
            // Whether the result cannot be get raise exception
            data.exception = new NoConnectionException();
        }
        return data;
    }

    private ApiData checkDates() {
        // Check if the system date/time matches with the remote date/time
        ApiData result = new ApiData();
        Date currentDateTime = Utility.getCurrentDateTime();
        TimeZone timeZone = TimeZone.getDefault();
        JSONObject jsonRoot = this.api.getJSONObject(String.format("dates/%s/%s/%s/%s/%s/",
                this.api.settings.getTabletID(),
                new SimpleDateFormat("yyyy-MM-dd").format(currentDateTime),
                new SimpleDateFormat("HH:mm.ss").format(currentDateTime),
                timeZone.getID().replace("/", ":"),
                timeZone.getDisplayName(Locale.ROOT).replace("/", ":")));
        if (jsonRoot != null) {
            try {
                // Check the status node for successful reads
                this.checkStatusResponse(jsonRoot);
                // Get current system date only
                Date date1 = Utility.getCurrentDate();
                // Get remote date
                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(jsonRoot.getString("date"));
                long difference = Math.abs(date1.getTime() - date2.getTime());
                // If the dates match then compare the time
                if (difference == 0) {
                    // Get current system time only
                    date1 = Utility.getCurrentTime();
                    // Get remote time
                    date2 = new SimpleDateFormat("HH:mm.ss").parse(jsonRoot.getString("time"));
                    // Find the difference in thirty seconds
                    difference = Math.abs(date1.getTime() - date2.getTime()) / 1000 / 30;
                }
                if (difference != 0) {
                    // Invalid date or time
                    result.exception = new InvalidDateTimeException();
                }
            } catch (InvalidServerStatusException exception) {
                exception.printStackTrace();
                result.exception = exception;
            } catch (InvalidResponseException exception) {
                exception.printStackTrace();
                result.exception = exception;
            } catch (ParseException exception) {
                exception.printStackTrace();
                result.exception = new InvalidResponseException();
            } catch (JSONException exception) {
                exception.printStackTrace();
                result.exception = new InvalidResponseException();
            }
        } else {
            // Whether the result cannot be get raise exception
            result.exception = new NoConnectionException();
        }
        return result;
    }

    private ApiData downloadData() {
        ApiData result = new ApiData();
        // Get data from the server
        JSONObject jsonRoot = this.api.getJSONObject(String.format("get/%s/%s/",
                this.api.settings.getTabletID(),
                this.api.getCurrentTokenCode()));
        if (jsonRoot != null) {
            try {
                // Check the status node for successful reads
                this.checkStatusResponse(jsonRoot);
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
            } catch (InvalidServerStatusException exception) {
                result.exception = exception;
            } catch (JSONException exception) {
                result.exception = new InvalidResponseException();
            } catch (ParseException exception) {
                result.exception = new InvalidResponseException();
            } catch (InvalidResponseException exception) {
                result.exception = exception;
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
                    // The activity was transmitted but it was already existing, this issue can be ignored
                    Log.w(TAG, String.format("Existing activity during the data transmission: %d", serviceActivity.id));
                } else if (status.equals(Api.STATUS_DIFFERENT_QUANTITY)) {
                    // The activity was transmitted but it was saved with a different quantity
                    Log.e(TAG, String.format("Activity %d already transmitted using a different quantity: %d",
                            serviceActivity.id,
                            serviceActivity.serviceQty));
                    throw new RetransmittedActivityException(
                            String.format(this.context.get().getString(R.string.sync_error_retransmitted_quantity),
                                    singleton.apiData.contractsMap.get(serviceActivity.contractId).employee.firstName,
                                    singleton.apiData.contractsMap.get(serviceActivity.contractId).employee.lastName,
                                    singleton.apiData.roomsStructureMap.get(serviceActivity.roomId).name,
                                    singleton.apiData.roomsBuildingMap.get(serviceActivity.roomId).name,
                                    singleton.apiData.roomsMap.get(serviceActivity.roomId).name,
                                    new SimpleDateFormat("yyyy-MM-dd").format(serviceActivity.date),
                                    serviceActivity.serviceQty
                            ));
                } else if (status.equals(Api.STATUS_DIFFERENT_DESCRIPTION)) {
                    // The activity was transmitted but it was saved with a different description
                    Log.e(TAG, String.format("Activity %d already transmitted using a different description: %s",
                            serviceActivity.id,
                            serviceActivity.description));
                    throw new RetransmittedActivityException(
                            String.format(this.context.get().getString(R.string.sync_error_retransmitted_description),
                                    singleton.apiData.contractsMap.get(serviceActivity.contractId).employee.firstName,
                                    singleton.apiData.contractsMap.get(serviceActivity.contractId).employee.lastName,
                                    singleton.apiData.roomsStructureMap.get(serviceActivity.roomId).name,
                                    singleton.apiData.roomsBuildingMap.get(serviceActivity.roomId).name,
                                    singleton.apiData.roomsMap.get(serviceActivity.roomId).name,
                                    new SimpleDateFormat("yyyy-MM-dd").format(serviceActivity.date),
                                    serviceActivity.description
                            ));
                } else if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException exception) {
                result.exception = new InvalidResponseException();
            } catch (InvalidResponseException exception) {
                result.exception = exception;
            } catch (RetransmittedActivityException exception) {
                result.exception = exception;
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
            this.callback.onProgress(this.currentStep, this.totalSteps);
        }
    }
}
