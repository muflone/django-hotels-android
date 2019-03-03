package com.muflone.android.django_hotels;

import android.content.Context;
import android.net.Uri;

import com.google.android.apps.authenticator.Base32String;
import com.muflone.android.django_hotels.otp.Token;

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
import java.util.TimeZone;

public class Api {
    private Context context;
    private Settings settings = null;
    private Uri apiUri;

    public Api(Context context) {
        this.context = context;
        this.settings = new Settings(context);
        this.apiUri = this.settings.getApiUri();
    }

    protected Uri buildUri(String segment) {
        // Return the Uri for the requested segment
        return Uri.withAppendedPath(this.apiUri, segment);
    }

    public Uri buildJsonUri(String segment) {
        // Return the Uri for the requested JSON API segment
        return Uri.withAppendedPath(this.buildUri("api/json/"), segment);
    }

    protected JSONObject getJSONObject(String segment) {
        // Return a JSONObject from the remote URL
        JSONObject jsonObject = null;
        try {
            URL requestUrl = new URL(this.buildJsonUri(segment).toString());
            URLConnection connection = requestUrl.openConnection();
            BufferedReader bufferedReader = bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonStringBuffer = new StringBuffer();
            // Save all the received text in jsonStringBuffer
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                jsonStringBuffer.append(line);
            }
            // Convert results to JSON
            jsonObject = new JSONObject(jsonStringBuffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean checkDates() {
        // Check if the system date/time matches with the remote date/time
        JSONObject jsonObject = this.getJSONObject("dates/");
        long difference = -1;
        if (jsonObject != null) {
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
                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("date"));
                difference = Math.abs(date1.getTime() - date2.getTime());
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
                    date2 = new SimpleDateFormat("hh:mm.ss").parse(jsonObject.getString("time"));
                    // Find the difference in thirty seconds
                    difference = Math.abs(date1.getTime() - date2.getTime()) / 1000 / 30;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return (difference == 0);
    }

    public String getCurrentTokenCode() {
        // Return the current TokenCode
        Token token = null;
        try {
            Uri uri = Uri.parse(String.format(
                    "otpauth://totp/MilazzoInn:Tablet %s?secret=%s&issuer=Muflone",
                    this.settings.getTabletID(),
                    Base32String.encode(this.settings.getTabletKey().getBytes())));
            token = new Token(uri);
        } catch (Token.TokenUriInvalidException e) {
            e.printStackTrace();
        }
        return token != null ? token.generateCodes().getCurrentCode() : null;
    }
}
