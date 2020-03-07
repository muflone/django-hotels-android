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
import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.dao.BuildingDao;
import com.muflone.android.django_hotels.database.dao.CommandDao;
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
import com.muflone.android.django_hotels.database.dao.TimestampDirectionDao;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.database.models.Structure;
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
import java.util.Objects;
import java.util.TimeZone;

public class TaskSync extends AsyncTask<Void, Void, TaskResult> {
    private final String TAG = getClass().getSimpleName();

    private final Api api;
    private final TaskListenerInterface callback;
    private final int totalSteps;
    private final Singleton singleton = Singleton.getInstance();
    private final WeakReference<Context> context;
    private int currentStep;

    public TaskSync(Context context, Api api, int totalSteps, TaskListenerInterface callback) {
        this.context = new WeakReference<>(context);
        this.api = api;
        this.callback = callback;
        this.currentStep = 0;
        this.totalSteps = totalSteps;
    }

    @Override
    protected TaskResult doInBackground(Void... params) {
        // Do the background job
        boolean transmissionErrors = false;
        ApiData data;

        // Check the server status
        this.updateProgress();
        data = this.requestApiStatus();
        if (data.exception == null) {
            // Check if the system date/time matches with the remote date/time
            this.updateProgress();
            data = this.requestApiDates();
            if (data.exception == null) {
                // Transmit any incomplete timestamp (UPLOAD)
                this.updateProgress();
                List<Timestamp> timestampsList = this.singleton.database.timestampDao().listByUntransmitted();
                for (Timestamp timestamp : timestampsList) {
                    data = this.requestApiPutTimestamp(timestamp);
                    if (data.exception == null) {
                        // Update transmission date
                        timestamp.transmission = Utility.getCurrentDateTime();
                        this.singleton.database.timestampDao().update(timestamp);
                    } else {
                        // There were some errors during the timestamps transmissions
                        transmissionErrors = true;
                    }
                }
                // Transmit any incomplete activity (UPLOAD)
                this.updateProgress();
                List<ServiceActivity> servicesActivityList = this.singleton.database.serviceActivityDao().listByUntransmitted();
                for (ServiceActivity serviceActivity : servicesActivityList) {
                    // Transmit data
                    data = this.requestApiPutActivity(serviceActivity);
                    if (data.exception == null) {
                        serviceActivity.transmission = Utility.getCurrentDateTime();
                        this.singleton.database.serviceActivityDao().update(serviceActivity);
                    } else {
                        // There were some errors during the timestamps transmissions
                        transmissionErrors = true;
                    }
                }
                // Transmit any incomplete extra (UPLOAD)
                this.updateProgress();
                List<ServiceActivity> extrasActivityList = this.singleton.database.serviceActivityDao().listExtrasByUntransmitted();
                for (ServiceActivity serviceActivity : extrasActivityList) {
                    // Transmit data
                    data = this.requestApiPutExtra(serviceActivity);
                    if (data.exception == null) {
                        serviceActivity.transmission = Utility.getCurrentDateTime();
                        this.singleton.database.serviceActivityDao().update(serviceActivity);
                    } else {
                        // There were some errors during the timestamps transmissions
                        transmissionErrors = true;
                    }
                }
                // If no errors were given during the upload proceed to the download
                if (! transmissionErrors) {
                    // Get new data from the server (DOWNLOAD)
                    this.updateProgress();
                    data = this.requestApiGet();
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
        return new TaskResult(data, data.exception);
    }

    @Override
    protected void onPostExecute(TaskResult result) {
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
            if (! jsonObject.getString("status").equals(Api.STATUS_OK)) {
                throw new InvalidServerStatusException(
                        String.format(
                            this.context.get().getString(R.string.sync_error_invalid_server_status_detail),
                                jsonObject.getString("status")));
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new InvalidResponseException();
        }
    }

    private ApiData requestApiStatus() {
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

    private ApiData requestApiDates() {
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

    private ApiData requestApiPutTimestamp(Timestamp timestamp) {
        ApiData result = new ApiData();
        JSONObject jsonRoot = null;
        // Send timestamps to the server
        try {
            jsonRoot = this.api.getJSONObject(String.format(Locale.ROOT,
                    "put/timestamp/%s/%s/%d/%d/%d/%d/%s/",
                    this.api.settings.getTabletID(),
                    this.api.getCurrentTokenCode(),
                    timestamp.contractId,
                    timestamp.structureId,
                    timestamp.directionId,
                    timestamp.datetime.getTime() / 1000,
                    URLEncoder.encode(timestamp.description.replace("\n", "\\n"), "UTF-8")));
        } catch (UnsupportedEncodingException exception) {
            result.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (status.equals(Api.STATUS_EXISTING)) {
                    Log.w(this.TAG, String.format("Existing timestamp during the data transmission: %s", status));
                } else if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(this.TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException exception) {
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

    private ApiData requestApiPutActivity(ServiceActivity serviceActivity) {
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
        } catch (UnsupportedEncodingException exception) {
            result.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (status.equals(Api.STATUS_EXISTING)) {
                    // The activity was transmitted but it was already existing, this issue can be ignored
                    Log.w(this.TAG, String.format("Existing activity during the data transmission: %d", serviceActivity.id));
                } else if (status.equals(Api.STATUS_DIFFERENT_QUANTITY)) {
                    // The activity was transmitted but it was saved with a different quantity
                    Log.e(this.TAG, String.format("Activity %d already transmitted using a different quantity: %d",
                            serviceActivity.id,
                            serviceActivity.serviceQty));
                    throw new RetransmittedActivityException(
                            String.format(this.context.get().getString(R.string.sync_error_retransmitted_quantity),
                                    Objects.requireNonNull(this.singleton.apiData.contractsMap.get(serviceActivity.contractId)).employee.firstName,
                                    Objects.requireNonNull(this.singleton.apiData.contractsMap.get(serviceActivity.contractId)).employee.lastName,
                                    Objects.requireNonNull(this.singleton.apiData.roomsStructureMap.get(serviceActivity.roomId)).name,
                                    Objects.requireNonNull(this.singleton.apiData.roomsBuildingMap.get(serviceActivity.roomId)).name,
                                    Objects.requireNonNull(this.singleton.apiData.roomsMap.get(serviceActivity.roomId)).name,
                                    new SimpleDateFormat("yyyy-MM-dd").format(serviceActivity.date),
                                    serviceActivity.serviceQty
                            ));
                } else if (status.equals(Api.STATUS_DIFFERENT_DESCRIPTION)) {
                    // The activity was transmitted but it was saved with a different description
                    Log.e(this.TAG, String.format("Activity %d already transmitted using a different description: %s",
                            serviceActivity.id,
                            serviceActivity.description));
                    throw new RetransmittedActivityException(
                            String.format(this.context.get().getString(R.string.sync_error_retransmitted_description),
                                    Objects.requireNonNull(this.singleton.apiData.contractsMap.get(serviceActivity.contractId)).employee.firstName,
                                    Objects.requireNonNull(this.singleton.apiData.contractsMap.get(serviceActivity.contractId)).employee.lastName,
                                    Objects.requireNonNull(this.singleton.apiData.roomsStructureMap.get(serviceActivity.roomId)).name,
                                    Objects.requireNonNull(this.singleton.apiData.roomsBuildingMap.get(serviceActivity.roomId)).name,
                                    Objects.requireNonNull(this.singleton.apiData.roomsMap.get(serviceActivity.roomId)).name,
                                    this.singleton.defaultDateFormatter.format(serviceActivity.date),
                                    serviceActivity.description
                            ));
                } else if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(this.TAG, String.format("Invalid response received during the data transmission: %s", status));
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

    private ApiData requestApiPutExtra(ServiceActivity serviceActivity) {
        ApiData result = new ApiData();
        JSONObject jsonRoot = null;
        // Send timestamps to the server
        try {
            jsonRoot = this.api.getJSONObject(String.format(Locale.ROOT,
                    "put/extra/%s/%s/%d/%d/%d/%d/%s/",
                    this.api.settings.getTabletID(),
                    this.api.getCurrentTokenCode(),
                    serviceActivity.contractId,
                    serviceActivity.structureId,
                    serviceActivity.serviceQty,
                    serviceActivity.date.getTime() / 1000,
                    URLEncoder.encode(serviceActivity.description.replace("\n", "\\n") , "UTF-8")));
        } catch (UnsupportedEncodingException exception) {
            result.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (! status.equals(Api.STATUS_OK)) {
                    // Invalid response received
                    Log.e(this.TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException exception) {
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

    private ApiData requestApiGet() {
        ApiData result = new ApiData();
        Iterator<String> jsonKeys;
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
                jsonKeys = jsonStructures.keys();
                while (jsonKeys.hasNext()) {
                    String key = jsonKeys.next();
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
                // Loop over every command
                JSONArray jsonCommands = jsonRoot.getJSONArray("commands");
                for (int i = 0; i < jsonCommands.length(); i++) {
                    Command objCommand = new Command(jsonCommands.getJSONObject(i));
                    result.commandsMap.put(objCommand.id, objCommand);
                }
            } catch (InvalidServerStatusException exception) {
                result.exception = exception;
            } catch (JSONException exception) {
                exception.printStackTrace();
                result.exception = new InvalidResponseException();
            } catch (ParseException exception) {
                exception.printStackTrace();
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

    private void saveToDatabase(ApiData data) {
        BrandDao brandDao = this.singleton.database.brandDao();
        BuildingDao buildingDao = this.singleton.database.buildingDao();
        CommandDao commandDao = this.singleton.database.commandDao();
        CompanyDao companyDao = this.singleton.database.companyDao();
        ContractDao contractDao = this.singleton.database.contractDao();
        ContractBuildingsDao contractBuildingsDao = this.singleton.database.contractBuildingsDao();
        ContractTypeDao contractTypeDao = this.singleton.database.contractTypeDao();
        JobTypeDao jobTypeDao = this.singleton.database.jobTypeDao();
        CountryDao countryDao = this.singleton.database.countryDao();
        EmployeeDao employeeDao = this.singleton.database.employeeDao();
        LocationDao locationDao = this.singleton.database.locationDao();
        RegionDao regionDao = this.singleton.database.regionDao();
        RoomDao roomDao = this.singleton.database.roomDao();
        ServiceDao serviceDao = this.singleton.database.serviceDao();
        StructureDao structureDao = this.singleton.database.structureDao();
        TimestampDirectionDao timestampDirectionDao = this.singleton.database.timestampDirectionDao();

        // Delete previous data
        commandDao.truncate();
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
            // Save extras
            for (Building extras : structure.extras) {
                countryDao.insert(extras.location.country);
                regionDao.insert(extras.location.region);
                locationDao.insert(extras.location);
                buildingDao.insert(extras);
                // Save rooms
                for (Room room : extras.rooms) {
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
        // Save data from commands
        for (Command command : data.commandsMap.values()) {
            commandDao.insert(command);
        }
    }

    private void updateProgress() {
        this.currentStep += 1;
        if (this.callback != null) {
            this.callback.onProgress(this.currentStep, this.totalSteps);
        }
    }
}
