package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "settings")
public class TabletSetting {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "data")
    public final String data;

    public TabletSetting(@NotNull String name, String data) {
        this.name = name;
        this.data = data;
    }

    public TabletSetting(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("name"),
                jsonObject.getString("data"));
    }

    public String getString() {
        // Return the data as string
        return this.data;
    }

    public int getInteger() {
        // Return the data as integer number
        return Integer.valueOf(this.data);
    }

    public long getLong() {
        // Return the data as long number
        return Long.valueOf(this.data);
    }

    public float getFloat() {
        // Return the data as float number
        return Float.valueOf(this.data);
    }

    public double getDouble() {
        // Return the data as double number
        return Double.valueOf(this.data);
    }

    public boolean getBoolean() {
        // Return the data as boolean
        return ! this.data.equals("0");
    }

    public Date getDate() {
        // Return the data as Date
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(this.data);
        } catch (ParseException error) {
            return null;
        }
    }

    public Date getTime() {
        // Return the data as Time
        try {
            return new SimpleDateFormat("HH:mm.ss").parse(this.data);
        } catch (ParseException error) {
            return null;
        }
    }


    public Date getDateTime() {
        // Return the data as Date and Time
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm.ss").parse(this.data);
        } catch (ParseException error) {
            return null;
        }
    }

    public byte[] decodeBase64() {
        // Return the data as a byte array from Base 64
        return Base64.decode(this.data, Base64.DEFAULT);
    }
}
