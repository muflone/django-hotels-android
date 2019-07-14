package com.muflone.android.django_hotels;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.tasks.TaskStructureUpdateEmployeeStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeStatus {
    public final Contract contract;
    public final Date date;
    public final List<TimestampDirection> timestampDirections;
    public final String[] directionsArray;
    public final boolean[] directionsCheckedArray;

    @SuppressWarnings("WeakerAccess")
    public EmployeeStatus(Contract contract, Date date,
                          List<TimestampDirection> timestampDirections,
                          List<Timestamp> timestampsEmployee) {
        this.contract = contract;
        this.date = date;
        // Initialize directionsArray and directionsCheckedArray
        this.timestampDirections = timestampDirections;
        this.directionsArray = new String[timestampDirections.size()];
        this.directionsCheckedArray = new boolean[timestampDirections.size()];
        // Get the already assigned timestamp directions to restore
        List<Long> assignedTimestampDirectionsList = new ArrayList<>();
        for (Timestamp timestamp : timestampsEmployee) {
            assignedTimestampDirectionsList.add(timestamp.directionId);
        }
        // Translate timestamp directions to array, needed by the AlertDialog for selections
        int index = 0;
        for (TimestampDirection direction : timestampDirections) {
            this.directionsArray[index] = direction.name;
            this.directionsCheckedArray[index] = assignedTimestampDirectionsList.contains(direction.id);
            index++;
        }
    }

    public void updateDatabase() {
        // Update database row
        new TaskStructureUpdateEmployeeStatus().execute(this);
    }
}
