package com.muflone.android.django_hotels;

import java.util.Date;

public class TimestampEmployeeItem {
    public final long id;
    public final String fullName;
    public final Date datetime;
    public final String direction;
    public Date transmission;

    public TimestampEmployeeItem(long id, String fullName, Date datetime,
                          String direction, Date transmission) {
        this.id = id;
        this.fullName = fullName;
        this.datetime = datetime;
        this.direction = direction;
        this.transmission = transmission;
    }
}
