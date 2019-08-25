/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskExtrasLoadEmployees extends AsyncTask<Void, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final List<String> employeesListInternal = new ArrayList<>();
    private final List<String> employeesList;
    private final WeakReference<ListView> employeesView;
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;

    @SuppressWarnings("WeakerAccess")
    public TaskExtrasLoadEmployees(@NonNull List<String> employeesList,
                                   @NonNull ListView employeesView,
                                   @NonNull Table<Long, Long, ServiceActivity> serviceActivityTable) {
        this.employeesList = employeesList;
        this.employeesView = new WeakReference<>(employeesView);
        this.serviceActivityTable = serviceActivityTable;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Load employees for the selected structure
        this.serviceActivityTable.clear();
        this.employeesListInternal.clear();
        for (Employee employee : this.singleton.selectedStructure.employees) {
            this.employeesListInternal.add(String.format("%s %s", employee.firstName, employee.lastName));
            // Reload services for contract
            Contract contract = Objects.requireNonNull(this.singleton.apiData.contractsMap.get(
                    employee.contractBuildings.get(0).contractId));
            for (ServiceActivity serviceActivity : this.singleton.database.serviceActivityDao().listExtrasByDateContract(
                    this.singleton.selectedDate, contract.id)) {
                this.serviceActivityTable.put(
                        contract.id,
                        serviceActivity.roomId,
                        serviceActivity);
            }
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
