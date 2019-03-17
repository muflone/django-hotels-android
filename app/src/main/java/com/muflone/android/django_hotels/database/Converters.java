package com.muflone.android.django_hotels.database;

import android.arch.persistence.room.TypeConverter;

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
}
