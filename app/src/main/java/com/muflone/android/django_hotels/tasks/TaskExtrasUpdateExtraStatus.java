package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.ExtraStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.util.List;

public class TaskExtrasUpdateExtraStatus extends AsyncTask<ExtraStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;

    @SuppressWarnings("WeakerAccess")
    public TaskExtrasUpdateExtraStatus(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        this.serviceActivityTable = serviceActivityTable;
    }

    @Override
    protected Void doInBackground(ExtraStatus... params) {
        ExtraStatus extraStatus = params[0];
        List<ServiceActivity> serviceActivityList =
                this.singleton.database.serviceActivityDao().listByDateContract(
                        this.singleton.selectedDate,
                        extraStatus.contractId, extraStatus.id);
        ServiceActivity serviceActivity;
        if (serviceActivityList.size() > 0) {
            serviceActivity = serviceActivityList.get(0);
            if (extraStatus.minutes > 0) {
                // Update existing ServiceActivity
                serviceActivity.serviceId = Constants.EXTRAS_SERVICE_ID;
                serviceActivity.description = extraStatus.description;
                serviceActivity.transmission = extraStatus.transmission;
                serviceActivity.serviceQty = extraStatus.minutes;
                this.singleton.database.serviceActivityDao().update(serviceActivity);
                serviceActivityTable.put(serviceActivity.contractId, serviceActivity.roomId, serviceActivity);
            } else {
                // Delete existing ServiceActivity
                this.singleton.database.serviceActivityDao().delete(serviceActivity);
                serviceActivityTable.remove(serviceActivity.contractId, serviceActivity.roomId);
            }
        } else if (extraStatus.minutes > 0) {
            // Create new ServiceActivity
            serviceActivity = new ServiceActivity(0,
                    this.singleton.selectedDate,
                    extraStatus.contractId,
                    extraStatus.structureId,
                    extraStatus.id,
                    Constants.EXTRAS_SERVICE_ID,
                    extraStatus.minutes,
                    true,
                    extraStatus.description,
                    null);
            this.singleton.database.serviceActivityDao().insert(serviceActivity);
            serviceActivityTable.put(serviceActivity.contractId, serviceActivity.roomId, serviceActivity);
        }
        return null;
    }
}

