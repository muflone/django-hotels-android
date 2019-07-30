package com.muflone.android.django_hotels;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.tasks.TaskExtrasUpdateExtraStatus;

import java.util.Date;

public class ExtraStatus {
    public final long contractId;
    public final long structureId;
    public final long id;
    public long minutes;
    public String description;
    public Date transmission;

    public ExtraStatus(long contractId, long structureId, long id, long minutes, String description, Date transmission) {
        this.contractId = contractId;
        this.structureId = structureId;
        this.id = id;
        this.minutes = minutes;
        this.description = description;
        this.transmission = transmission;
    }

    public void updateDatabase(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        // Update database row
        new TaskExtrasUpdateExtraStatus(serviceActivityTable).execute(this);
    }
}