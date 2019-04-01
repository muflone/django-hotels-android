package com.muflone.android.django_hotels.fragments;

import android.content.Context;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.Structure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructuresFragment extends Fragment {
    private final ApiData apiData = Singleton.getInstance().apiData;

    private View rootLayout;
    private Structure selectedStructure;
    private TabLayout structuresTabs;
    private ListView employeesView;
    private ExpandableListView roomsView;
    private ExpandableListAdapter buildingRoomsAdapter;
    private final List<String> employeesList = new ArrayList<>();
    private final List<String> buildingsList = new ArrayList<>();
    private final HashMap<String, List<RoomStatus>> roomsList = new HashMap<>();
    private final List<Structure> structures = new ArrayList<>();
    private final List<Service> roomServicesList = new ArrayList<>();

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
        // Initialize UI
        this.loadUI(inflater, container);

        this.employeesView.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, this.employeesList));
        this.employeesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                employeesView.requestFocusFromTouch();
                employeesView.setSelection(position);
                loadEmployee(selectedStructure.employees.get(position));
            }
        });

        // Initialize Structures
        this.loadStructures();
        this.structuresTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadEmployees(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Select the first structure tab
        if (this.structuresTabs.getTabCount() > 0 ) {
            this.loadEmployees(this.structuresTabs.getTabAt(0));
        }

        // Build services list for rooms (the first element is empty)
        this.roomServicesList.add(null);
        for (Service service : this.apiData.serviceMap.values()) {
            // Show only services with show_in_app = true
            if (service.show_in_app) {
                this.roomServicesList.add(service);
            }
        }

        this.buildingRoomsAdapter = new ExpandableListAdapter(getActivity(), buildingsList, roomsList);
        this.roomsView.setAdapter(this.buildingRoomsAdapter);
        this.roomsView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setExpandableListViewHeight(parent, groupPosition);
                return false;
            }
        });
        return this.rootLayout;
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
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
        this.roomsView = rootLayout.findViewById(R.id.roomsView);
    }

    private void loadStructures() {
        // Load all the structures, skipping those without any employee
        this.structuresTabs.removeAllTabs();
        for (Structure structure : this.apiData.structuresMap.values()) {
            if (structure.employees.size() > 0) {
                TabLayout.Tab tabItem = this.structuresTabs.newTab();
                this.structures.add(structure);
                tabItem.setText(structure.name);
                this.structuresTabs.addTab(tabItem);
            }
        }
    }

    private void loadEmployees(TabLayout.Tab tab) {
        // Load employees list for the selected Structure tab
        this.employeesList.clear();
        this.selectedStructure = this.structures.get(tab.getPosition());
        for (Employee employee : this.selectedStructure.employees) {
            this.employeesList.add(String.format("%s %s", employee.firstName, employee.lastName));
        }

        // Update data in the list
        ((ArrayAdapter) this.employeesView.getAdapter()).notifyDataSetChanged();
        // Select the first employee for the selected tab
        if (this.employeesList.size() > 0) {
            this.employeesView.post(new Runnable() {
                @Override
                public void run() {
                    employeesView.requestFocusFromTouch();
                    employeesView.setSelection(0);
                    loadEmployee(selectedStructure.employees.get(0));
                }
            });
        }
    }

    private void loadEmployee(Employee employee) {
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
        // Get the first contract for the employee
        Contract contract = this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId);
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
        // Build buildings and rooms lists
        buildingsList.clear();
        roomsList.clear();
        for (ContractBuildings contractBuilding : employee.contractBuildings) {
            Building building = this.apiData.buildingsMap.get(contractBuilding.buildingId);
            this.buildingsList.add(building.name);
            List<RoomStatus> rooms = new ArrayList<>();
            for (Room room : building.rooms) {
                // TODO: restore the previous service for the room
                rooms.add(new RoomStatus(getActivity(), room.name, null));
            }
            this.roomsList.put(building.name, rooms);
        }
        this.buildingRoomsAdapter.notifyDataSetChanged();
        // Collapse all the buildings groups
        // TODO: implement initial collapse/expansion as preference
        for (int group = 0; group < this.buildingsList.size(); group++) {
            this.roomsView.collapseGroup(group);
        }
        // Allocate space for the expanded list
        this.setExpandableListViewHeight(this.roomsView, -1);
    }

    private void setExpandableListViewHeight(ExpandableListView listView, int group) {
        // Set the ListView height
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int index = 0; index < listAdapter.getGroupCount(); index++) {
            View groupItem = listAdapter.getGroupView(index, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(index)) && (index != group))
                    || ((!listView.isGroupExpanded(index)) && (index == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(index); j++) {
                    View listItem = listAdapter.getChildView(index, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
                // Add Divider Height
                totalHeight += listView.getDividerHeight() * (listAdapter.getChildrenCount(index) - 1);
            }
        }
        // Add Divider Height
        totalHeight += listView.getDividerHeight() * (listAdapter.getGroupCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        params.height = height < 10 ? 200 : height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Context context;
        private final List<String> buildingsList;
        private final HashMap<String, List<RoomStatus>> roomsList;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<RoomStatus>> listChildData) {
            this.context = context;
            this.buildingsList = listDataHeader;
            this.roomsList = listChildData;
        }

        @Override
        public RoomStatus getChild(int groupPosition, int childPosition) {
            return this.roomsList.get(this.buildingsList.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final RoomStatus roomStatus = getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.structures_building_item, parent, false);
            }

            // Set room name
            TextView roomView = convertView.findViewById(R.id.roomView);
            roomView.setText(roomStatus.name);

            // Set room service
            Button buttonState = convertView.findViewById(R.id.serviceButton);
            buttonState.setText(roomStatus.getServiceName());
            buttonState.setOnClickListener(new View.OnClickListener() {
                public void onClick(View button) {
                    roomStatus.nextService(roomServicesList);
                    ((Button) button).setText(roomStatus.getServiceName());
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.roomsList.get(this.buildingsList.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.buildingsList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.buildingsList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.structures_building_group, parent, false);
            }
            TextView buildingView = convertView.findViewById(R.id.buildingView);
            buildingView.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class RoomStatus {
        private final String emptyServiceDescription;
        public final String name;
        public Service service;
        private int serviceCounter = 0;

        public RoomStatus(Context context, String name, Service service) {
            this.emptyServiceDescription = context.getString(R.string.empty_service);
            this.name = name;
            this.service = service;
        }

        public Service nextService(List<Service> services) {
            // Cycle services
            this.serviceCounter++;
            if (this.serviceCounter == services.size()) {
                this.serviceCounter = 0;
            }
            this.service = services.get(this.serviceCounter);
            return this.service;
        }

        public String getServiceName() {
            // Get current service name
            return this.service == null ? this.emptyServiceDescription : this.service.name;
        }
    }
}
