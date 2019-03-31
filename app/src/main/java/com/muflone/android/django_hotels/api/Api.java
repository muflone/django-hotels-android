package com.muflone.android.django_hotels.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.authenticator.Base32String;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.otp.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Api {
    private final String TAG = "Api";
    private final String STATUS_OK = "OK";
    private final String STATUS_EXISTING = "EXISTING";
    public final Settings settings;
    public final Context context;

    public Api(Context context) {
        this.context = context;
        this.settings = Singleton.getInstance().settings;
    }

    private Uri buildUri(String segment) {
        // Return the Uri for the requested segment
        return Uri.withAppendedPath(this.settings.getApiUri(), segment);
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
            if (!jsonObject.getString("status").equals(this.STATUS_OK)) {
                throw new InvalidResponseException();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidResponseException();
        }
    }

    public ApiData checkDates() {
        // Check if the system date/time matches with the remote date/time
        ApiData data = new ApiData();
        JSONObject jsonRoot = this.getJSONObject("dates/");
        if (jsonRoot != null) {
            try {
                // Get current system date only
                Date date1 = Utility.getCurrentDate(this.settings.getTimeZone());
                // Get remote date
                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(jsonRoot.getString("date"));
                long difference = Math.abs(date1.getTime() - date2.getTime());
                // If the dates match then compare the time
                if (difference == 0) {
                    // Get current system time only
                    date1 = Utility.getCurrentTime(this.settings.getTimeZone());
                    // Get remote time
                    date2 = new SimpleDateFormat("HH:mm.ss").parse(jsonRoot.getString("time"));
                    // Find the difference in thirty seconds
                    difference = Math.abs(date1.getTime() - date2.getTime()) / 1000 / 30;
                }
                if (difference != 0) {
                    // Invalid date or time
                    data.exception = new InvalidDateTimeException();
                }
            } catch (ParseException exception) {
                exception.printStackTrace();
                data.exception = new InvalidResponseException();
            } catch (JSONException exception) {
                exception.printStackTrace();
                data.exception = new InvalidResponseException();
            }
        } else {
            // Whether the result cannot be get raise exception
            data.exception = new NoConnectionException();
        }
        return data;
    }

    public ApiData getData() {
        ApiData data = new ApiData();
        // Get data from the server
        JSONObject jsonRoot = this.getJSONObject(String.format("get/%s/%s/",
                this.settings.getTabletID(),
                this.getCurrentTokenCode()));
        if (jsonRoot != null) {
            try {
                // Loop over every structure
                JSONObject jsonStructures = jsonRoot.getJSONObject("structures");
                Iterator<?> jsonKeys = jsonStructures.keys();
                while (jsonKeys.hasNext()) {
                    String key = (String) jsonKeys.next();
                    Structure objStructure = new Structure(jsonStructures.getJSONObject(key));
                    data.structuresMap.put(objStructure.id, objStructure);
                }
                // Loop over every contract
                JSONArray jsonContracts = jsonRoot.getJSONArray("contracts");
                for (int i = 0; i < jsonContracts.length(); i++) {
                    Contract contract = new Contract(jsonContracts.getJSONObject(i));
                    data.contractsMap.put(contract.id, contract);
                }
                // Loop over every service
                JSONArray jsonServices = jsonRoot.getJSONArray("services");
                for (int i = 0; i < jsonServices.length(); i++) {
                    Service service = new Service(jsonServices.getJSONObject(i));
                    if (service.extra_service) {
                        data.serviceExtraMap.put(service.id, service);
                    } else {
                        data.serviceMap.put(service.id, service);
                    }
                }
                // Loop over every timestamp direction
                JSONArray jsonTimestampDirections = jsonRoot.getJSONArray("timestamp_directions");
                for (int i = 0; i < jsonTimestampDirections.length(); i++) {
                    TimestampDirection timestampDirection = new TimestampDirection(jsonTimestampDirections.getJSONObject(i));
                    data.timestampDirectionsMap.put(timestampDirection.id, timestampDirection);
                }
                // Check the final node for successful reads
                this.checkStatusResponse(jsonRoot);
            } catch (JSONException e) {
                data.exception = new InvalidResponseException();
            } catch (ParseException e) {
                data.exception = new InvalidResponseException();
            } catch (InvalidResponseException e) {
                data.exception = e;
            }
        } else {
            // Unable to download data from the server
            data.exception = new NoDownloadException();
        }
        return data;
    }

    public ApiData putTimestamp(Timestamp timestamp) {
        JSONObject jsonRoot = null;
        ApiData data = new ApiData();
        // Send timestamps to the server
        try {
            jsonRoot = this.getJSONObject(String.format("put/timestamp/%s/%s/%d/%d/%d/%s/",
                    this.settings.getTabletID(),
                    this.getCurrentTokenCode(),
                    timestamp.contractId,
                    timestamp.directionId,
                    timestamp.datetime.getTime() / 1000,
                    URLEncoder.encode(timestamp.description, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            data.exception = new InvalidResponseException();
        }
        if (jsonRoot != null) {
            try {
                Log.d("", jsonRoot.getString("status"));
                // Check the final node for successful reads
                String status = jsonRoot.getString("status");
                if (status.equals(this.STATUS_EXISTING)) {
                    Log.w(TAG, String.format("Existing timestamp during the data transmission: %s", status));
                } else if (! status.equals(this.STATUS_OK)) {
                    // Invalid response received
                    Log.e(TAG, String.format("Invalid response received during the data transmission: %s", status));
                    throw new InvalidResponseException();
                }
            } catch (JSONException e) {
                data.exception = new InvalidResponseException();
            } catch (InvalidResponseException e) {
                data.exception = e;
            }
        } else {
            // Unable to download data from the server
            data.exception = new NoDownloadException();
        }
        return data;
    }
}
