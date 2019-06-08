package com.muflone.android.django_hotels.api;

import android.net.Uri;

import com.google.android.apps.authenticator.Base32String;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;

import org.fedorahosted.freeotp.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Api {
    public static final String STATUS_OK = "OK";
    public static final String STATUS_EXISTING = "EXISTING";
    public static final String STATUS_DIFFERENT_QUANTITY = "QUANTITY";
    public static final String STATUS_DIFFERENT_DESCRIPTION = "DESCRIPTION";

    public final Settings settings;

    public Api() {
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

    public JSONObject getJSONObject(String segment) {
        // Return a JSONObject from the remote URL
        JSONObject jsonObject = null;
        try {
            URL requestUrl = new URL(this.buildJsonUri(segment).toString());
            URLConnection connection = requestUrl.openConnection();
            // Add custom headers
            connection.setRequestProperty("client-agent", this.settings.getPackageName());
            connection.setRequestProperty("client-version", this.settings.getApplicationVersion());

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
}
