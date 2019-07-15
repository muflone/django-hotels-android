package com.muflone.android.django_hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.muflone.android.django_hotels.ContractViewsUpdater;
import com.muflone.android.django_hotels.EmployeeViewsUpdater;
import com.muflone.android.django_hotels.ExtraStatus;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.tasks.TaskExtrasLoadEmployees;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExtrasFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private final ApiData apiData = this.singleton.apiData;
    private final List<String> employeesList = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private final HashMap<Long, List<ExtraStatus>> extraStatusMap = new HashMap<>();

    private Context context;
    private View rootLayout;
    private ListView employeesView;
    private ListView extrasView;
    private EmployeeViewsUpdater employeeViewsUpdater;
    private ContractViewsUpdater contractViewsUpdater;
    private long extrasServiceId;
    private CustomAdapter extrasAdapter;
    private List<ExtraStatus> extraStatusList;

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
        // Load default extras service ID
        this.extrasServiceId = this.singleton.settings.getLong(CommandConstants.SETTING_EXTRAS_SERVICE_ID, -1);
        // Prepare Extras adapter and layout
        this.extraStatusList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ExtraStatus extraStatus = new ExtraStatus(this.context, 0, i, null, "sample " + i, null);
            this.extraStatusList.add(extraStatus);
        }
        this.extrasAdapter = new CustomAdapter(this.extraStatusList, this.context);
        this.extrasAdapter.notifyDataSetChanged();
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
        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.extrasView = rootLayout.findViewById(R.id.extrasView);
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
        new TaskExtrasLoadEmployees(this.employeesList, this.employeesView).execute();
    }

    private void loadEmployee(Employee employee) {
        // Get the first contract for the employee
        Contract contract = Objects.requireNonNull(this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId));
        // Update Employee and Contract details
        this.employeeViewsUpdater.updateViews(employee);
        this.contractViewsUpdater.updateViews(contract);
    }

    public static class CustomAdapter extends ArrayAdapter<ExtraStatus> implements View.OnClickListener {
        private List<ExtraStatus> dataSet;
        Context context;

        // View lookup cache
        private static class ViewHolder {
            TextView extraView;
        }

        public CustomAdapter(List<ExtraStatus> data, Context context) {
            super(context, R.layout.extras_extra_item, data);
            this.dataSet = data;
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            ExtraStatus dataModel = Objects.requireNonNull(this.getItem(position));

            switch (view.getId())
            {
                case R.id.extraView:
                    Snackbar.make(view, dataModel.description, Snackbar.LENGTH_SHORT)
                            .setAction("No action", null).show();
                    break;
            }
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            // Get the data item for this position
            ExtraStatus dataModel = Objects.requireNonNull(getItem(position));
            // Check if an existing view is being reused, otherwise inflate the view
            CustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            if (convertView == null) {
                viewHolder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.extras_extra_item, parent, false);
                viewHolder.extraView = convertView.findViewById(R.id.extraView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.extraView.setText(dataModel.description);
            viewHolder.extraView.setOnClickListener(this);
            viewHolder.extraView.setTag(position);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
