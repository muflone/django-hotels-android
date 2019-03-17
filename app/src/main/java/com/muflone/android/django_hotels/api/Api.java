package com.muflone.android.django_hotels.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.apps.authenticator.Base32String;
import com.muflone.android.django_hotels.AsyncTaskRunnerListener;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.BrandDao;
import com.muflone.android.django_hotels.database.dao.BuildingDao;
import com.muflone.android.django_hotels.database.dao.CompanyDao;
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
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.otp.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

public class Api {
    private final Settings settings;
    private final Uri apiUri;
    private final Context context;

    public Api(Context context) {
        this.context = context;
        this.settings = new Settings(this.context);
        this.apiUri = this.settings.getApiUri();
    }

    private Uri buildUri(String segment) {
        // Return the Uri for the requested segment
        return Uri.withAppendedPath(this.apiUri, segment);
    }

    private Uri buildJsonUri(String segment) {
        // Return the Uri for the requested JSON API segment
        return Uri.withAppendedPath(this.buildUri("api/v1/"), segment);
    }

    private JSONObject getJSONObject(String segment) {
        // Return a JSONObject from the remote URL
        JSONObject jsonObject = null;
        try {
            URL requestUrl = new URL(this.buildJsonUri(segment).toString());
            URLConnection connection = requestUrl.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonStringBuilder = new StringBuilder();
            // Save all the received text in jsonStringBuilder
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                jsonStringBuilder.append(line);
            }
            // Convert results to JSON
            jsonObject = new JSONObject(jsonStringBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getCurrentTokenCode() {
        // Return the current TokenCode
        Token token = null;
        try {
            Uri uri = Uri.parse(String.format(
                    "otpauth://totp/Hotels:Tablet %s?secret=%s&issuer=Muflone",
                    this.settings.getTabletID(),
                    Base32String.encode(this.settings.getTabletKey().getBytes())));
            token = new Token(uri);
        } catch (Token.TokenUriInvalidException e) {
            e.printStackTrace();
        }
        return token != null ? token.generateCodes().getCurrentCode() : null;
    }

    private void checkStatusResponse(JSONObject jsonObject) throws InvalidResponseException {
        // Check the status object for valid data
        try {
            if (!jsonObject.getString("status").equals("OK")) {
                throw new InvalidResponseException();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidResponseException();
        }
    }

    private GetDataResults getData(String tabletId, String tokenCode) {
        GetDataResults results = new GetDataResults();
        // Check if the system date/time matches with the remote date/time
        JSONObject jsonRoot = this.getJSONObject("dates/");
        if (jsonRoot != null) {
            try {
                // Get current system date only
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone(this.settings.getTimeZone()));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date date1 = calendar.getTime();
                // Get remote date
                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(jsonRoot.getString("date"));
                long difference = Math.abs(date1.getTime() - date2.getTime());
                // If the dates match then compare the time
                if (difference == 0) {
                    // Get current system time only
                    calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone(this.settings.getTimeZone()));
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.MONTH, 0);
                    calendar.set(Calendar.YEAR, 1970);
                    date1 = calendar.getTime();
                    // Get remote time
                    date2 = new SimpleDateFormat("HH:mm.ss").parse(jsonRoot.getString("time"));
                    // Find the difference in thirty seconds
                    difference = Math.abs(date1.getTime() - date2.getTime()) / 1000 / 30;
                }
                if (difference != 0) {
                    // Invalid date or time
                    results.exception = new InvalidDateTimeException();
                }
            } catch (ParseException exception) {
                exception.printStackTrace();
                results.exception = new InvalidResponseException();
            } catch (JSONException exception) {
                exception.printStackTrace();
                results.exception = new InvalidResponseException();
            }
        } else {
            // Whether the result cannot be get raise exception
            results.exception = new NoConnectionException();
        }
        if (results.exception == null) {
            // Get data from the server
            jsonRoot = this.getJSONObject(String.format("get/%s/%s/", tabletId, tokenCode));
            if (jsonRoot != null) {
                try {
                    // Loop over every structure
                    JSONObject jsonStructures = jsonRoot.getJSONObject("structures");
                    Iterator<?> jsonKeys = jsonStructures.keys();
                    while (jsonKeys.hasNext()) {
                        String key = (String) jsonKeys.next();
                        Structure objStructure = new Structure(jsonStructures.getJSONObject(key));
                        results.structures.add(objStructure);
                    }
                    // Loop over every contract
                    JSONArray jsonContracts = jsonRoot.getJSONArray("contracts");
                    for (int i = 0; i < jsonContracts.length(); i++) {
                        results.contracts.add(new Contract(jsonContracts.getJSONObject(i)));
                    }
                    // Check the final node for successful reads
                    this.checkStatusResponse(jsonRoot);
                } catch (JSONException e) {
                    results.exception = new InvalidResponseException();
                } catch (ParseException e) {
                    results.exception = new InvalidResponseException();
                } catch (InvalidResponseException e) {
                    results.exception = e;
                }
            } else {
                // Unable to download data from the server
                results.exception = new NoDownloadException();
            }
        }
        return results;
    }

    public void getData(AsyncTaskRunnerListener callback) {
        AsyncTaskRunner task = new AsyncTaskRunner(this, callback);
        task.execute();
    }

    /*
     * AsyncTask(Params, Progress, Result)
     */
    private static class AsyncTaskRunner extends AsyncTask<Void, Void, GetDataResults> {
        private final Api api;
        private final AsyncTaskRunnerListener callback;

        AsyncTaskRunner(Api api, AsyncTaskRunnerListener callback) {
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
            if (this.callback != null) {
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
            ContractTypeDao contractTypeDao = database.contractTypeDao();
            JobTypeDao jobTypeDao = database.jobTypeDao();
            CountryDao countryDao = database.countryDao();
            EmployeeDao employeeDao = database.employeeDao();
            LocationDao locationDao = database.locationDao();
            RegionDao regionDao = database.regionDao();
            RoomDao roomDao = database.roomDao();
            StructureDao structureDao = database.structureDao();

            // Delete previous data
            structureDao.truncate();
            buildingDao.truncate();
            companyDao.truncate();
            contractTypeDao.truncate();
            jobTypeDao.truncate();
            countryDao.truncate();
            locationDao.truncate();
            regionDao.truncate();
            brandDao.truncate();
            roomDao.truncate();
            contractDao.truncate();

            // Save data from structures
            for (Structure structure : results.structures) {
                brandDao.insert(structure.brand);
                companyDao.insert(structure.company);
                locationDao.insert(structure.location);
                regionDao.insert(structure.location.region);
                countryDao.insert(structure.location.country);
                structureDao.insert(structure);
                // Save buildings
                for (Building building : structure.buildings) {
                    locationDao.insert(building.location);
                    regionDao.insert(building.location.region);
                    countryDao.insert(building.location.country);
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
            }
            return;
        }
    }
}
