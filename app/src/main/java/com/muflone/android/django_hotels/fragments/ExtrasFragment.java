/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
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

package com.muflone.android.django_hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.ContractViewsUpdater;
import com.muflone.android.django_hotels.EmployeeViewsUpdater;
import com.muflone.android.django_hotels.ExtraStatus;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.tasks.TaskExtrasLoadEmployees;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExtrasFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private final ApiData apiData = this.singleton.apiData;
    private final List<String> employeesList = new ArrayList<>();
    private final List<ExtraStatus> extraStatusList = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private static final Table<Long, Long, ServiceActivity> serviceActivityTable = HashBasedTable.create();

    private Context context;
    private View rootLayout;
    private ScrollView scrollView;
    private ListView employeesView;
    private ListView extrasView;
    private AppCompatButton addExtraButton;
    private EmployeeViewsUpdater employeeViewsUpdater;
    private ContractViewsUpdater contractViewsUpdater;
    private Employee currentEmployee;
    private CustomAdapter extrasAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START EXTRA BEGIN commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_EXTRA_BEGIN);
        // Initialize UI
        this.loadUI(inflater, Objects.requireNonNull(container));

        this.employeesView.setAdapter(new ArrayAdapter<>(
                this.context, android.R.layout.simple_list_item_activated_1, this.employeesList));
        this.employeesView.setOnItemClickListener(
                (parent, view, position, id) -> loadEmployee(this.singleton.selectedStructure.employees.get(position)));
        this.addExtraButton.setOnClickListener(view -> {
            List<ServiceActivity> serviceActivityList = new ArrayList<>(serviceActivityTable.row(
                    currentEmployee.contractBuildings.get(0).contractId).values());
            // Get last ExtraStatus ID
            long extraId = serviceActivityList.size() > 0 ? serviceActivityList.get(serviceActivityList.size() - 1).roomId : 0;
            // Check the maximum number of allowed extras
            int max_extras = this.singleton.selectedStructure.extras.size() > 0 ?
                    this.singleton.selectedStructure.extras.get(0).rooms.size() : 0;
            if (serviceActivityList.size() < max_extras) {
                extraId++;
                // Create new ExtraStatus object
                ExtraStatus extraStatus = new ExtraStatus(
                        currentEmployee.contractBuildings.get(0).contractId,
                        this.singleton.selectedStructure.id,
                        extraId,
                        0,
                        String.format(Locale.ROOT, "Extra %d", extraId),
                        null);
                // Add a new serviceActivity in order to reserve the next id
                serviceActivityTable.put(extraStatus.contractId, extraId,
                        new ServiceActivity(0,
                                ExtrasFragment.this.singleton.selectedDate,
                                extraStatus.contractId,
                                extraStatus.structureId,
                                extraStatus.id,
                                Constants.EXTRAS_SERVICE_ID,
                                extraStatus.minutes,
                                true,
                                extraStatus.description,
                                extraStatus.transmission));
                ExtrasFragment.this.extraStatusList.add(extraStatus);
                ExtrasFragment.this.extrasAdapter.notifyDataSetChanged();
                ExtrasFragment.this.extrasView.post(() -> ExtrasFragment.this.scrollView.fullScroll(View.FOCUS_DOWN));
            } else if (max_extras == 0) {
                // No extras allowed for this structure, unable to add a new extra
                Toast.makeText(this.context, R.string.extras_no_extras_allowed, Toast.LENGTH_SHORT).show();
            } else {
                // Max number of extras reached, unable to add a new extra
                Toast.makeText(this.context, R.string.extras_max_number_of_extras, Toast.LENGTH_SHORT).show();
            }
        });
        // Prepare Extras adapter and layout
        this.extrasAdapter = new CustomAdapter(this.context, this.extraStatusList, serviceActivityTable);
        this.extrasView.setAdapter(this.extrasAdapter);
        // Load the employees for the selected structure
        if (this.singleton.selectedStructure != null) {
            this.loadEmployees();
        }
        // Execute START EXTRA END commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_EXTRA_END);
        return this.rootLayout;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.extras_fragment, container, false);
        // Save references
        this.scrollView = rootLayout.findViewById(R.id.scrollView);
        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.extrasView = rootLayout.findViewById(R.id.extrasView);
        this.addExtraButton = rootLayout.findViewById(R.id.addExtraButton);
        // Prepares employee views updater
        this.employeeViewsUpdater = new EmployeeViewsUpdater(
                rootLayout.findViewById(R.id.employeeIdView),
                rootLayout.findViewById(R.id.employeeFirstNameView),
                rootLayout.findViewById(R.id.employeeLastNameView),
                rootLayout.findViewById(R.id.employeeGenderImageView)
        );
        // Prepares contracts views updater
        this.contractViewsUpdater = new ContractViewsUpdater(
                rootLayout.findViewById(R.id.contractIdView),
                rootLayout.findViewById(R.id.contractCompanyView),
                rootLayout.findViewById(R.id.contractStatusView),
                rootLayout.findViewById(R.id.contractTypeView),
                rootLayout.findViewById(R.id.contractDailyHoursView),
                rootLayout.findViewById(R.id.contractWeeklyHoursView),
                rootLayout.findViewById(R.id.contractStartDateView),
                rootLayout.findViewById(R.id.contractEndDateView)
        );
    }

    private void loadEmployees() {
        // Load employees list for the selected Structure tab
        this.employeesList.clear();
        serviceActivityTable.clear();
        new TaskExtrasLoadEmployees(this.employeesList, this.employeesView, serviceActivityTable).execute();
    }

    private void loadEmployee(Employee employee) {
        this.currentEmployee = employee;
        // Get the first contract for the employee
        Contract contract = Objects.requireNonNull(this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId));
        // Update Employee and Contract details
        this.employeeViewsUpdater.updateViews(employee);
        this.contractViewsUpdater.updateViews(contract);
        // Update adapter
        this.extraStatusList.clear();
        for (ServiceActivity serviceActivity : serviceActivityTable.row(contract.id).values()) {
            // Skip empty extras on reload
            if (serviceActivity.serviceQty > 0) {
                ExtraStatus extraStatus = new ExtraStatus(
                        serviceActivity.contractId,
                        this.singleton.selectedStructure.id,
                        serviceActivity.roomId,
                        serviceActivity.serviceQty,
                        serviceActivity.description,
                        serviceActivity.transmission);
                this.extraStatusList.add(extraStatus);
            }
        }
        this.extrasAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("WeakerAccess")
    public static class CustomAdapter extends ArrayAdapter<ExtraStatus> {
        private final Singleton singleton = Singleton.getInstance();
        private final long extrasTimeStep;
        private final Table<Long, Long, ServiceActivity> serviceActivityTable;
        private final Context context;

        // View lookup cache
        private static class ViewHolder {
            private TextView descriptionView;
            private AppCompatButton increaseButton;
            private AppCompatButton decreaseButton;
            private TextView extraView;
            private ImageButton noteButton;
            private ImageView transmissionImage;
        }

        public CustomAdapter(Context context, List<ExtraStatus> data, Table<Long, Long, ServiceActivity> serviceActivityTable) {
            super(context, R.layout.extras_extra_item, data);
            this.context = context;
            this.serviceActivityTable = serviceActivityTable;
            // Load default time step
            this.extrasTimeStep = this.singleton.settings.getLong(CommandConstants.SETTING_EXTRAS_TIME_STEP, 15);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            CustomAdapter.ViewHolder viewHolder;
            ExtraStatus extraStatus = Objects.requireNonNull(this.getItem(position));
            if (convertView == null) {
                // Get the data item for this position
                // Check if an existing view is being reused, otherwise inflate the view
                viewHolder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.extras_extra_item, parent, false);
                viewHolder.descriptionView = convertView.findViewById(R.id.descriptionView);
                viewHolder.increaseButton = convertView.findViewById(R.id.increaseButton);
                viewHolder.decreaseButton = convertView.findViewById(R.id.decreaseButton);
                viewHolder.extraView = convertView.findViewById(R.id.extraView);
                viewHolder.noteButton = convertView.findViewById(R.id.noteButton);
                viewHolder.transmissionImage = convertView.findViewById(R.id.transmissionImage);
                convertView.setTag(viewHolder);
            } else {
                // View lookup cache stored in tag
                viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
            }
            viewHolder.descriptionView.setText(extraStatus.description);
            this.updateExtraView(viewHolder, extraStatus, 0);
            // Set extra description Click
            viewHolder.noteButton.setOnClickListener(button -> {
                final EditText descriptionView = new EditText(context);
                descriptionView.setSingleLine();
                descriptionView.setText(extraStatus.description);
                descriptionView.setSelection(extraStatus.description.length());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.description);
                alertDialogBuilder.setView(descriptionView);
                alertDialogBuilder.setCancelable(true)
                        .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                            if (! singleton.apiData.isValidContract(extraStatus.contractId)) {
                                // Cannot change a disabled contract
                                Toast.makeText(context, R.string.extras_unable_to_use_contract,
                                        Toast.LENGTH_SHORT).show();
                            } else if (extraStatus.transmission != null) {
                                // Cannot change an already transmitted extra
                                Toast.makeText(context,
                                        R.string.extras_unable_to_change_transmitted_activity,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                extraStatus.description = descriptionView.getText().toString();
                                extraStatus.updateDatabase(serviceActivityTable);
                                updateExtraView(viewHolder, extraStatus, 0);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.cancel());
                // Show the alert dialog
                alertDialogBuilder.create().show();
            });
            // Set increase time button
            viewHolder.increaseButton.setTag(position);
            viewHolder.increaseButton.setOnClickListener(view -> CustomAdapter.this.updateExtraView(
                    viewHolder, extraStatus, CustomAdapter.this.extrasTimeStep));
            // Set decrease time button
            viewHolder.decreaseButton.setTag(position);
            viewHolder.decreaseButton.setOnClickListener(view -> CustomAdapter.this.updateExtraView(
                    viewHolder, extraStatus, 0 - CustomAdapter.this.extrasTimeStep));
            // Define transmissionImage
            viewHolder.transmissionImage.setImageResource(extraStatus.transmission == null ?
                    R.drawable.ic_timestamp_untransmitted : R.drawable.ic_timestamp_transmitted);
            viewHolder.transmissionImage.setOnLongClickListener(button -> {
                extraStatus.transmission = null;
                extraStatus.updateDatabase(serviceActivityTable);
                viewHolder.transmissionImage.setImageResource(R.drawable.ic_timestamp_untransmitted);
                Toast.makeText(context,
                        R.string.extras_marked_extra_as_untransmitted,
                        Toast.LENGTH_SHORT).show();
                return false;
            });
            // Return the completed view to render on screen
            return convertView;
        }

        private void updateExtraView(@NotNull ViewHolder viewHolder, @NotNull ExtraStatus extraStatus, long step) {
            if (step != 0 && extraStatus.transmission != null) {
                // Cannot change an already transmitted extra
                Toast.makeText(context,
                        R.string.extras_unable_to_change_transmitted_activity,
                        Toast.LENGTH_SHORT).show();
                // Disable changes
                step = 0;
            }
            // Set the extra time
            if (step > 0) {
                // Increase time
                extraStatus.minutes += step;
            } else if (step < 0) {
                // Decrease time
                step = Math.abs(step);
                extraStatus.minutes -= Math.min(extraStatus.minutes, step);
            }
            // Update extra view time
            long minutes = extraStatus.minutes % 60;
            long hours = (extraStatus.minutes - minutes) / 60;
            String extraViewTime;
            if (hours == 1 & minutes > 0) {
                extraViewTime = this.context.getString(R.string.extras_time_hour_minutes, hours, minutes);
            } else if (hours == 1) {
                extraViewTime = this.context.getString(R.string.extras_time_hour, hours);
            } else if (hours > 1 & minutes > 0) {
                extraViewTime = this.context.getString(R.string.extras_time_hours_minutes, hours, minutes);
            } else if (hours > 1) {
                extraViewTime = this.context.getString(R.string.extras_time_hours, hours);
            } else if (minutes > 0) {
                extraViewTime = this.context.getString(R.string.extras_time_minutes, minutes);
            } else {
                extraViewTime = this.context.getString(R.string.extras_time_zero);
            }
            viewHolder.extraView.setText(extraViewTime);
            viewHolder.descriptionView.setText(extraStatus.description);
            // Update database
            if (step != 0) {
                extraStatus.updateDatabase(serviceActivityTable);
            }
        }
    }
}
