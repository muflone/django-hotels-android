package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.fragments.StructuresFragment;

import java.util.List;

public class TaskStructureUpdateEmployeeStatus extends AsyncTask<StructuresFragment.EmployeeStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();

    // Update database for EmployeeStatus
    @Override
    protected Void doInBackground(StructuresFragment.EmployeeStatus... params) {
        StructuresFragment.EmployeeStatus employeeStatus = params[0];
        List<Timestamp> timestampsEmployee = this.singleton.database.timestampDao().listByContractNotEnterExit(
                employeeStatus.date, employeeStatus.contract.id);
        // Delete any previous timestamp
        this.singleton.database.timestampDao().delete(timestampsEmployee.toArray(new Timestamp[0]));
        // Re-add every active timestamp
        timestampsEmployee.clear();
        for (int index = 0; index < employeeStatus.timestampDirections.size(); index++) {
            if (employeeStatus.directionsCheckedArray[index]) {
                TimestampDirection timestampDirection = employeeStatus.timestampDirections.get(index);
                timestampsEmployee.add(new Timestamp(0, employeeStatus.contract.id,
                        timestampDirection.id, employeeStatus.date,"", null));
            }
        }
        this.singleton.database.timestampDao().insert(timestampsEmployee.toArray(new Timestamp[0]));
        return null;
    }
}
