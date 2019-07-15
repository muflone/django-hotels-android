package com.muflone.android.django_hotels;

import android.content.Context;

import com.muflone.android.django_hotels.database.models.Service;

import java.util.Date;

public class ExtraStatus {
    public final long contractId;
    public final long id;
    public Service service;
    public String description;
    public Date transmission;

    public ExtraStatus(Context context, long contractId, long id,
                       Service service, String description, Date transmission) {
        this.contractId = contractId;
        this.id = id;
        this.service = service;
        this.description = description;
        this.transmission = transmission;
    }

    /*
    public void updateDatabase(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        // Update database row
        new TaskStructureUpdateRoomStatus(serviceActivityTable).execute(this);
    }
    */
}