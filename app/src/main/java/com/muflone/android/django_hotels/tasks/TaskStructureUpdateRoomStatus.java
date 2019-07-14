package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.RoomStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.util.List;

public class TaskStructureUpdateRoomStatus extends AsyncTask<RoomStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;

    @SuppressWarnings("WeakerAccess")
    public TaskStructureUpdateRoomStatus(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        this.serviceActivityTable = serviceActivityTable;
    }

    @Override
    protected Void doInBackground(RoomStatus... params) {
        RoomStatus roomStatus = params[0];
        List<ServiceActivity> serviceActivityList =
                this.singleton.database.serviceActivityDao().listByDateContract(
                        this.singleton.selectedDate,
                        roomStatus.contractId, roomStatus.roomId);
        ServiceActivity serviceActivity;
        if (serviceActivityList.size() > 0) {
            serviceActivity = serviceActivityList.get(0);
            if (roomStatus.service != null) {
                // Update existing ServiceActivity
                serviceActivity.serviceId = roomStatus.service.id;
                serviceActivity.description = roomStatus.description;
                serviceActivity.transmission = roomStatus.transmission;
                this.singleton.database.serviceActivityDao().update(serviceActivity);
                serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                        serviceActivity);
            } else {
                // Delete existing ServiceActivity
                this.singleton.database.serviceActivityDao().delete(serviceActivity);
                serviceActivityTable.remove(roomStatus.contractId, roomStatus.roomId);
            }
        } else if (roomStatus.service != null) {
            // Create new ServiceActivity
            serviceActivity = new ServiceActivity(0,
                    this.singleton.selectedDate,
                    roomStatus.contractId,
                    roomStatus.roomId,
                    roomStatus.service.id,
                    1, roomStatus.description, null);
            this.singleton.database.serviceActivityDao().insert(serviceActivity);
            serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                    serviceActivity);
        }
        return null;
    }
}

