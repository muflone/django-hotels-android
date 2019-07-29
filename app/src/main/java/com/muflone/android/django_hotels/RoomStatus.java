package com.muflone.android.django_hotels;

import android.content.Context;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.tasks.TaskStructureUpdateRoomStatus;

import java.util.Date;
import java.util.List;

public class RoomStatus {
    private final String emptyServiceDescription;
    private final List<Service> services;
    private int serviceCounter;
    public final long contractId;
    public final long structureId;
    public final long roomId;
    public final String name;
    public Service service;
    public String description;
    public Date transmission;

    public RoomStatus(Context context, String name, long contractId, long structureId, long roomId,
               List<Service> services, Service service, String description,
               Date transmission) {
        this.emptyServiceDescription = context.getString(R.string.empty_service);
        this.name = name;
        this.contractId = contractId;
        this.structureId = structureId;
        this.roomId = roomId;
        this.services = services;
        this.service = service;
        this.serviceCounter = this.services.indexOf(this.service);
        this.description = description;
        this.transmission = transmission;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Service nextService() {
        // Cycle services
        this.serviceCounter++;
        if (this.serviceCounter == this.services.size()) {
            this.serviceCounter = 0;
        }
        this.service = this.services.get(this.serviceCounter);
        return this.service;
    }

    public String getServiceName() {
        // Get current service name
        return this.service == null ? this.emptyServiceDescription : this.service.name;
    }

    public void prepareForNothing() {
        // Set the cycle counter to the last element so that the next service will be Nothing
        this.serviceCounter = this.services.size() - 1;
    }

    public void updateDatabase(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        // Update database row
        new TaskStructureUpdateRoomStatus(serviceActivityTable).execute(this);
    }
}