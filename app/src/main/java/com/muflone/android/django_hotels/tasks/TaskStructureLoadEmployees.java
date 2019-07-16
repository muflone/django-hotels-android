package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.EmployeeStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskStructureLoadEmployees extends AsyncTask<Void, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final List<String> employeesListInternal = new ArrayList<>();
    private final List<String> employeesList;
    private final WeakReference<ListView> employeesView;
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;
    private final List<EmployeeStatus> employeesStatusList;
    private final HashMap<Long, List<Long>> roomsEmployeesAssignedList;

    @SuppressWarnings("WeakerAccess")
    public TaskStructureLoadEmployees(@NonNull List<String> employeesList,
                                      @NonNull ListView employeesView,
                                      @NonNull Table<Long, Long, ServiceActivity> serviceActivityTable,
                                      @NonNull List<EmployeeStatus> employeesStatusList,
                                      @NonNull HashMap<Long, List<Long>> roomsEmployeesAssignedList) {
        this.employeesList = employeesList;
        this.employeesView = new WeakReference<>(employeesView);
        this.serviceActivityTable = serviceActivityTable;
        this.employeesStatusList = employeesStatusList;
        this.roomsEmployeesAssignedList = roomsEmployeesAssignedList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Load employees for the selected structure
        this.employeesListInternal.clear();
        for (Employee employee : this.singleton.selectedStructure.employees) {
            this.employeesListInternal.add(String.format("%s %s", employee.firstName, employee.lastName));
            // Reload services for contract
            Contract contract = Objects.requireNonNull(this.singleton.apiData.contractsMap.get(
                    employee.contractBuildings.get(0).contractId));
            for (ServiceActivity serviceActivity : this.singleton.database.serviceActivityDao().listByDateContract(
                    this.singleton.selectedDate, contract.id)) {
                this.serviceActivityTable.put(
                        serviceActivity.contractId,
                        serviceActivity.roomId,
                        serviceActivity);
                // Add employee to the already assigned room list
                // only if the room belongs to the selected structure
                if (this.roomsEmployeesAssignedList.containsKey(serviceActivity.roomId)) {
                    Objects.requireNonNull(this.roomsEmployeesAssignedList.get(
                            serviceActivity.roomId)).add(employee.id);
                }
            }
            // Add employee status
            this.employeesStatusList.add(new EmployeeStatus(contract,
                    this.singleton.selectedDate,
                    this.singleton.apiData.timestampDirectionsNotEnterExit,
                    this.singleton.database.timestampDao().listByContractNotEnterExit(
                            this.singleton.selectedDate, contract.id)));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Update data in the list
        this.employeesList.clear();
        this.employeesList.addAll(this.employeesListInternal);
        ((ArrayAdapter) this.employeesView.get().getAdapter()).notifyDataSetChanged();
        // Select the first employee for the selected tab
        if (this.employeesList.size() > 0) {
            this.employeesView.get().performItemClick(
                    this.employeesView.get().getAdapter().getView(0, null, null),
                    0,
                    this.employeesView.get().getAdapter().getItemId(0)
            );
        }
    }
}
