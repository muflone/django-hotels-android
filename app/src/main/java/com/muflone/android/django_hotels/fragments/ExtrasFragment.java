package com.muflone.android.django_hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import java.util.Locale;
import java.util.Objects;

public class ExtrasFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private final ApiData apiData = this.singleton.apiData;
    private final List<String> employeesList = new ArrayList<>();
    private final List<ExtraStatus> extraStatusList = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private final HashMap<Long, List<ExtraStatus>> extrasStatusMap = new HashMap<>();

    private Context context;
    private View rootLayout;
    private ScrollView scrollView;
    private ListView employeesView;
    private ListView extrasView;
    private AppCompatButton addExtraButton;
    private EmployeeViewsUpdater employeeViewsUpdater;
    private ContractViewsUpdater contractViewsUpdater;
    private Employee currentEmployee;
    private long extrasServiceId;
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
        this.addExtraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ExtraStatus> extraStatusList = ExtrasFragment.this.extrasStatusMap.get(currentEmployee.id);
                // Get last ExtraStatus ID
                long extraId = extraStatusList.size() > 0 ? extraStatusList.get(extraStatusList.size() - 1).id : 0;
                // Create new ExtraStatus object
                extraId++;
                ExtraStatus extraStatus = new ExtraStatus(ExtrasFragment.this.singleton.settings.context,
                        currentEmployee.contractBuildings.get(0).contractId,
                        extraId,
                        0,
                        String.format(Locale.ROOT, "Extra %d", extraId),
                        null);
                extraStatusList.add(extraStatus);
                ExtrasFragment.this.extraStatusList.add(extraStatus);
                ExtrasFragment.this.extrasAdapter.notifyDataSetChanged();
                ExtrasFragment.this.extrasView.post(() -> ExtrasFragment.this.scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });
        // Load default extras service ID
        this.extrasServiceId = this.singleton.settings.getLong(CommandConstants.SETTING_EXTRAS_SERVICE_ID, -1);
        // Prepare Extras adapter and layout
        this.extrasAdapter = new CustomAdapter(this.extraStatusList, this.context);
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
        new TaskExtrasLoadEmployees(this.employeesList, this.employeesView, this.extrasStatusMap).execute();
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
        this.extraStatusList.addAll(this.extrasStatusMap.get(employee.id));
        this.extrasAdapter.notifyDataSetChanged();
    }

    public static class CustomAdapter extends ArrayAdapter<ExtraStatus> {
        private Context context;

        // View lookup cache
        private static class ViewHolder {
            TextView descriptionView;
            AppCompatButton extraButton;
            ImageButton noteButton;
            ImageView transmissionImage;
        }

        public CustomAdapter(List<ExtraStatus> data, Context context) {
            super(context, R.layout.extras_extra_item, data);
            this.context = context;
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            CustomAdapter.ViewHolder viewHolder;
            ExtraStatus dataModel = Objects.requireNonNull(this.getItem(position));
            if (convertView == null) {
                // Get the data item for this position
                // Check if an existing view is being reused, otherwise inflate the view
                viewHolder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.extras_extra_item, parent, false);
                viewHolder.descriptionView = convertView.findViewById(R.id.descriptionView);
                viewHolder.extraButton = convertView.findViewById(R.id.extraButton);
                viewHolder.noteButton = convertView.findViewById(R.id.noteButton);
                viewHolder.transmissionImage = convertView.findViewById(R.id.transmissionImage);
                convertView.setTag(viewHolder);
            } else {
                // View lookup cache stored in tag
                viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
            }
            viewHolder.descriptionView.setText(dataModel.description);
            viewHolder.extraButton.setText(String.valueOf(dataModel.id));
            // Define transmissionImage
            viewHolder.transmissionImage.setImageResource(dataModel.transmission == null ?
                    R.drawable.ic_timestamp_untransmitted : R.drawable.ic_timestamp_transmitted);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
