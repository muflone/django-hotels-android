package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

public class ReportTimestamp {
    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "datetime")
    public Date datetime;

    @ColumnInfo(name = "direction")
    public String direction;
}
