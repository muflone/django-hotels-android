package com.muflone.android.django_hotels.database;

import android.arch.persistence.room.TypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        // Convert Date to Long
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        // Convert Long to Date
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static JSONObject stringToJSONObject(String value) {
        // Convert String to JSONObject
        try {
            return new JSONObject(value);
        } catch (JSONException e) {
            return null;
        }
    }

    @TypeConverter
    public static String jsonObjectToString(JSONObject value) {
        // Convert a JSONObject to String
        return value.toString();
    }
}
