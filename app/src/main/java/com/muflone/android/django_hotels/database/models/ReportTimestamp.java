package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import java.util.Date;

public class ReportTimestamp {
    @ColumnInfo(name = "first_name")
    @NonNull
    public String firstName;

    @ColumnInfo(name = "last_name")
    @NonNull
    public String lastName;

    @ColumnInfo(name = "datetime")
    @NonNull
    public Date datetime;

    @ColumnInfo(name = "direction")
    @NonNull
    public String direction;
}
