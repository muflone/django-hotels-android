package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Structure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StructuresFragment extends Fragment {
    private final ApiData apiData = Singleton.getInstance().apiData;

    private View rootLayout;
    private Structure selectedStructure;
    private TabLayout structuresTabs;
    private ListView employeesView;
    private List<String> employeesList = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();

    private TextView firstNameView;
    private TextView lastNameView;
    private ImageView genderImageView;
    private TextView companyView;
    private TextView contractTypeView;
    private TextView dailyHoursView;
    private TextView weeklyHoursView;
    private TextView startDateView;
    private TextView endDateView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.loadUI(inflater, container);
        this.employeesView.setAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, this.employeesList));
        this.employeesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                employeesView.requestFocusFromTouch();
                employeesView.setSelection(position);
                loadEmployee(selectedStructure.employees.get(position));
            }
        });


        // Initialize Structures
        this.structuresTabs.removeAllTabs();
        for (Structure structure : this.apiData.structuresMap.values()) {
            TabLayout.Tab tabItem = this.structuresTabs.newTab();
            this.structures.add(structure);
            tabItem.setText(structure.name);
            this.structuresTabs.addTab(tabItem);
        }
        this.structuresTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadEmployees(tab);
                // Select the first employee for the selected tab
                if (employeesList.size() > 0) {
                    employeesView.requestFocusFromTouch();
                    employeesView.setSelection(0);
                    loadEmployee(selectedStructure.employees.get(0));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Select the first structure tab
        if (employeesList.size() > 0) {
            this.structuresTabs.post(new Runnable() {
                @Override
                public void run() {
                    loadEmployee(selectedStructure.employees.get(0));
                }
            });
        }
        return rootLayout;
    }

    protected void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.structures_fragment, container, false);
        // Save references
        this.structuresTabs = rootLayout.findViewById(R.id.structuresLayout);
        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.firstNameView = rootLayout.findViewById(R.id.firstNameView);
        this.lastNameView = rootLayout.findViewById(R.id.lastNameView);
        this.genderImageView = rootLayout.findViewById(R.id.genderImageView);
        this.companyView = rootLayout.findViewById(R.id.companyView);
        this.contractTypeView = rootLayout.findViewById(R.id.contractTypeView);
        this.dailyHoursView = rootLayout.findViewById(R.id.dailyHoursView);
        this.weeklyHoursView = rootLayout.findViewById(R.id.weeklyHoursView);
        this.startDateView = rootLayout.findViewById(R.id.startDateView);
        this.endDateView = rootLayout.findViewById(R.id.endDateView);
    }

    protected void loadEmployees(TabLayout.Tab tab) {
        // Load employess list for the selected Structure tab
        employeesList.clear();
        this.selectedStructure = this.structures.get(tab.getPosition());
        for (Employee employee : this.selectedStructure.employees) {
            employeesList.add(String.format("%s %s", employee.firstName, employee.lastName));
        }
        // Update data
        ((ArrayAdapter) employeesView.getAdapter()).notifyDataSetChanged();
    }

    protected void loadEmployee(Employee employee) {
        // Load Employee details
        this.firstNameView.setText(employee.firstName);
        this.lastNameView.setText(employee.lastName);
        int genderResourceId;
        switch (employee.gender) {
            case "male":
                genderResourceId = R.drawable.ic_gender_male;
                break;
            case "female":
                genderResourceId = R.drawable.ic_gender_female;
                break;
            default:
                genderResourceId = R.drawable.ic_gender_unknown;
                break;
        }
        this.genderImageView.setImageResource(genderResourceId);
        ContractBuildings contractBuildings = employee.contractBuildings.get(0);
        Contract contract = this.apiData.contractsMap.get(contractBuildings.contractId);
        this.companyView.setText(this.apiData.companiesMap.get(contract.companyId).name);
        // Add contract info
        ContractType contractType = this.apiData.contractTypeMap.get(contract.contractTypeId);
        this.contractTypeView.setText(contractType.name);
        this.dailyHoursView.setText(String.valueOf(contractType.dailyHours));
        this.weeklyHoursView.setText(String.valueOf(contractType.weeklyHours));
        // Add contract dates
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.startDateView.setText(formatter.format(contract.startDate));
        this.endDateView.setText(formatter.format(contract.endDate));
    }
}
