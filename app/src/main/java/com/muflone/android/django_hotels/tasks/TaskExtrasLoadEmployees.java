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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskExtrasLoadEmployees extends AsyncTask<Void, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final List<String> employeesList;
    private final WeakReference<ListView> employeesView;

    @SuppressWarnings("WeakerAccess")
    public TaskExtrasLoadEmployees(@NonNull List<String> employeesList,
                                   @NonNull ListView employeesView) {
        this.employeesList = employeesList;
        this.employeesView = new WeakReference<>(employeesView);
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Load employees for the selected structure
        for (Employee employee : this.singleton.selectedStructure.employees) {
            this.employeesList.add(String.format("%s %s", employee.firstName, employee.lastName));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Update data in the list
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
