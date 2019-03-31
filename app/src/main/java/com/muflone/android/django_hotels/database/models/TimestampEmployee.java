package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import java.util.Date;

public class TimestampEmployee {
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "first_name")
    public final String firstName;

    @ColumnInfo(name = "last_name")
    public final String lastName;

    @ColumnInfo(name = "datetime")
    @NonNull
    public final Date datetime;

    @ColumnInfo(name = "direction")
    @NonNull
    public final String direction;

    public TimestampEmployee(long id, String firstName, String lastName, @NonNull Date date,
                             @NonNull String direction) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.datetime = datetime;
        this.direction = direction;
    }
}
