package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.support.annotation.NonNull;

import java.util.Date;

public class TimestampEmployee {
    @ColumnInfo(name = "first_name")
    public final String firstName;

    @ColumnInfo(name = "last_name")
    public final String lastName;

    @ColumnInfo(name = "date")
    @NonNull
    public final Date date;

    @ColumnInfo(name = "time")
    @NonNull
    public final Date time;

    public TimestampEmployee(String firstName, String lastName, @NonNull Date date, @NonNull Date time) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.time = time;
    }
}
