/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
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

package com.muflone.android.django_hotels.api;

import android.net.Uri;

import com.google.android.apps.authenticator.Base32String;
import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.commands.CommandConstants;

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
            // Set connection connect timeout (don't allow lower timeout than default)
            int timeout = settings.getInteger(
                    CommandConstants.SETTING_SYNC_CONNECT_TIMEOUT,
                    Constants.SYNC_CONNECT_TIMEOUT_DEFAULT);
            connection.setConnectTimeout(timeout > Constants.SYNC_CONNECT_TIMEOUT_DEFAULT ? timeout :
                    Constants.SYNC_CONNECT_TIMEOUT_DEFAULT);
            // Set connection read timeout (don't allow lower timeout than default)
            timeout = settings.getInteger(
                    CommandConstants.SETTING_SYNC_READ_TIMEOUT,
                    Constants.SYNC_READ_TIMEOUT_DEFAULT);
            connection.setReadTimeout(timeout > Constants.SYNC_READ_TIMEOUT_DEFAULT ? timeout :
                    Constants.SYNC_READ_TIMEOUT_DEFAULT);
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
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (JSONException exception) {
            exception.printStackTrace();
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
        } catch (Token.TokenUriInvalidException exception) {
            exception.printStackTrace();
        }
        return token != null ? token.generateCodes().getCurrentCode() : null;
    }
}
