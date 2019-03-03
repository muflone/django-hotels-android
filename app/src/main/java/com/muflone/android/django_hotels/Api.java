package com.muflone.android.django_hotels;

import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
}
