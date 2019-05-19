package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class StructuresFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private final Api api = this.singleton.api;
    private final ApiData apiData = this.singleton.apiData;

    private Context context;
    private View rootLayout;
    private Structure selectedStructure;
    private TabLayout structuresTabs;
    private ListView employeesView;
    private ExpandableListView roomsView;
    private ExpandableListAdapter buildingRoomsAdapter;
    private final List<String> employeesList = new ArrayList<>();
    private final List<EmployeeStatus> employeesStatusList = new ArrayList<>();
    private final List<String> buildingsList = new ArrayList<>();
    private final HashMap<String, List<RoomStatus>> roomsList = new HashMap<>();
    private final List<Structure> structures = new ArrayList<>();
    private final List<Service> roomServicesList = new ArrayList<>();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable = HashBasedTable.create();
    private final HashMap<Long, List<Long>> roomsEmployeesAssignedList = new HashMap<>();
    private final HashMap<String, Boolean> buildingsClosedStatusMap = new HashMap<>();

    private TextView employeeIdView;
    private TextView employeeFirstNameView;
    private TextView employeeLastNameView;
    private ImageView employeeGenderImageView;
    private TextView contractIdView;
    private TextView contractCompanyView;
    private TextView contractStatusView;
    private TextView contractTypeView;
    private TextView contractDailyHoursView;
    private TextView contractWeeklyHoursView;
    private TextView contractStartDateView;
    private TextView contractEndDateView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize UI
        this.loadUI(inflater, container);

        this.employeesView.setAdapter(new ArrayAdapter<>(
                this.context, android.R.layout.simple_list_item_activated_1, this.employeesList));
        this.employeesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadEmployee(selectedStructure.employees.get(position));
            }
        });
        this.employeesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Show contextual menu for employee
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.structures_employee_set_conditions);
                // Get service directions list
                EmployeeStatus employeeStatus = employeesStatusList.get(position);
                builder.setMultiChoiceItems(employeeStatus.directionsArray, employeeStatus.directionsCheckedArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @SuppressWarnings("EmptyMethod")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            }
                        });
                // Save choices when the OK button is pressed
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        employeeStatus.updateDatabase();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
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
        if (this.structuresTabs.getTabCount() > 0) {
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

        this.buildingRoomsAdapter = new ExpandableListAdapter(this.context, buildingsList, roomsList);
        this.roomsView.setAdapter(this.buildingRoomsAdapter);
        this.roomsView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                String groupName = parent.getExpandableListAdapter().getGroup(groupPosition).toString();
                buildingsClosedStatusMap.put(groupName, ! buildingsClosedStatusMap.get(groupName));
                setExpandableListViewHeight(parent, groupPosition);
                return false;
            }
        });
        return this.rootLayout;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.structures_fragment, container, false);
        // Save references
        this.structuresTabs = rootLayout.findViewById(R.id.structuresLayout);
        this.employeeIdView = rootLayout.findViewById(R.id.employeeIdView);
        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.employeeFirstNameView = rootLayout.findViewById(R.id.employeeFirstNameView);
        this.employeeLastNameView = rootLayout.findViewById(R.id.employeeLastNameView);
        this.employeeGenderImageView = rootLayout.findViewById(R.id.employeeGenderImageView);
        this.contractIdView = rootLayout.findViewById(R.id.contractIdView);
        this.contractCompanyView = rootLayout.findViewById(R.id.contractCompanyView);
        this.contractStatusView = rootLayout.findViewById(R.id.contractStatusView);
        this.contractTypeView = rootLayout.findViewById(R.id.contractTypeView);
        this.contractDailyHoursView = rootLayout.findViewById(R.id.contractDailyHoursView);
        this.contractWeeklyHoursView = rootLayout.findViewById(R.id.contractWeeklyHoursView);
        this.contractStartDateView = rootLayout.findViewById(R.id.contractStartDateView);
        this.contractEndDateView = rootLayout.findViewById(R.id.contractEndDateView);
        this.roomsView = rootLayout.findViewById(R.id.roomsView);
    }

    private void loadStructures() {
        // Load all the structures, skipping those without any employee
        this.structuresTabs.removeAllTabs();
        SortedSet<Structure> sortedStructures = new TreeSet<>(apiData.structuresMap.values());
        for (Structure structure : sortedStructures) {
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
        this.employeesStatusList.clear();
        this.roomsEmployeesAssignedList.clear();
        this.serviceActivityTable.clear();
        this.selectedStructure = this.structures.get(tab.getPosition());
        // Initialize buildings groups to collapsed
        this.buildingsClosedStatusMap.clear();
        for (Building building : this.selectedStructure.buildings) {
            this.buildingsClosedStatusMap.put(building.name,
                    this.api.settings.getBuildingsInitiallyClosed());
            // Initialize empty already assigned rooms list (<Room ID><List of employees>)
            for (Room room : building.rooms) {
                this.roomsEmployeesAssignedList.put(room.id, new ArrayList<>());
            }
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                AppDatabase database = AppDatabase.getAppDatabase(context);
                // Load employees for the selected structure
                for (Employee employee : selectedStructure.employees) {
                    employeesList.add(String.format("%s %s", employee.firstName, employee.lastName));
                    // Reload services for contract
                    Contract contract = apiData.contractsMap.get(employee.contractBuildings.get(0).contractId);
                    for (ServiceActivity serviceActivity : database.serviceActivityDao().listByDateContract(
                            singleton.selectedDate, contract.id)) {
                        serviceActivityTable.put(
                                serviceActivity.contractId,
                                serviceActivity.roomId,
                                serviceActivity);
                        // Add employee to the already assigned room list
                        // only if the room belongs to the selected structure
                        if (roomsEmployeesAssignedList.containsKey(serviceActivity.roomId)) {
                            roomsEmployeesAssignedList.get(serviceActivity.roomId).add(employee.id);
                        }
                    }
                    // Add employee status
                    employeesStatusList.add(new EmployeeStatus(contract,
                            singleton.selectedDate,
                            apiData.timestampDirectionsNotEnterExit,
                            database.timestampDao().listByContractNotEnterExit(
                                    singleton.selectedDate, contract.id)));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Update data in the list
                ((ArrayAdapter) employeesView.getAdapter()).notifyDataSetChanged();
                // Select the first employee for the selected tab
                if (employeesList.size() > 0) {
                    employeesView.performItemClick(
                            employeesView.getAdapter().getView(0, null, null),
                            0,
                            employeesView.getAdapter().getItemId(0)
                    );
                }
            }
        }.execute();
    }

    private void loadEmployee(Employee employee) {
        // Load Employee details
        this.employeeIdView.setText(String.valueOf(employee.id));
        this.employeeFirstNameView.setText(employee.firstName);
        this.employeeLastNameView.setText(employee.lastName);
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
        this.employeeGenderImageView.setImageResource(genderResourceId);
        // Get the first contract for the employee
        Contract contract = this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId);
        this.contractIdView.setText(String.valueOf(contract.id));
        this.contractCompanyView.setText(this.apiData.companiesMap.get(contract.companyId).name);
        // Add contract info
        ContractType contractType = this.apiData.contractTypeMap.get(contract.contractTypeId);
        this.contractStatusView.setText(contract.enabled ? R.string.enabled : R.string.disabled);
        this.contractTypeView.setText(contractType.name);
        this.contractDailyHoursView.setText(String.valueOf(contractType.dailyHours));
        this.contractWeeklyHoursView.setText(String.valueOf(contractType.weeklyHours));
        // Add contract dates
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.contractStartDateView.setText(formatter.format(contract.startDate));
        this.contractEndDateView.setText(formatter.format(contract.endDate));
        // Build buildings and rooms lists
        this.buildingsList.clear();
        this.roomsList.clear();
        for (ContractBuildings contractBuilding : employee.contractBuildings) {
            Building building = this.apiData.buildingsMap.get(contractBuilding.buildingId);
            // Only show the buildings for the current structure
            if (building.structureId == this.selectedStructure.id) {
                this.buildingsList.add(building.name);
                List<RoomStatus> rooms = new ArrayList<>();
                Service service;
                String description;
                Date transmission;
                for (Room room : building.rooms) {
                    // Restore the previous service for the room
                    if (this.serviceActivityTable.contains(contractBuilding.contractId, room.id)) {
                        service = apiData.serviceMap.get(
                                this.serviceActivityTable.get(contractBuilding.contractId, room.id).serviceId);
                        description = this.serviceActivityTable.get(contractBuilding.contractId, room.id).description;
                        transmission = this.serviceActivityTable.get(contractBuilding.contractId, room.id).transmission;
                    } else {
                        service = null;
                        description = "";
                        transmission = null;
                    }
                    RoomStatus roomStatus = new RoomStatus(this.context, room.name,
                            contractBuilding.contractId, room.id, this.roomServicesList,
                            service, description, transmission);
                    rooms.add(roomStatus);
                }
                this.roomsList.put(building.name, rooms);
            }
        }
        this.buildingRoomsAdapter.notifyDataSetChanged();
        // Collapse all the buildings groups
        for (int group = 0; group < this.buildingsList.size(); group++) {
            // Restore previous groups opened status
            if (this.buildingsClosedStatusMap.get(this.buildingsList.get(group))) {
                this.roomsView.collapseGroup(group);
            } else {
                this.roomsView.expandGroup(group);
            }
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
        private final ApiData apiData;
        private Drawable descriptionEnabledDrawable;
        private Drawable descriptionDisabledDrawable;

        ExpandableListAdapter(Context context, List<String> listDataHeader,
                              HashMap<String, List<RoomStatus>> listChildData) {
            this.context = context;
            this.buildingsList = listDataHeader;
            this.roomsList = listChildData;
            this.apiData = singleton.apiData;
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
            RoomStatus roomStatus = getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.structures_building_item, parent, false);
            }
            // This reference is used from the inner classes
            View rowView = convertView;
            // Update database row
            roomStatus.updateDatabase();
            // Update view row
            this.updateRoomView(convertView, roomStatus);
            // Handle service present image LongClick
            ImageView servicePresentImage = convertView.findViewById(R.id.servicePresentImage);
            servicePresentImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (roomsEmployeesAssignedList.get(roomStatus.roomId).size() > 0) {
                        showAlreadyAssignedEmployees(roomsEmployeesAssignedList.get(roomStatus.roomId));
                    }
                    return false;
                }
            });
            // Set transmission LongClick
            ImageView transmissionImage = convertView.findViewById(R.id.transmissionImage);
            transmissionImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View button) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... result) {
                            // Delete transmission date
                            roomStatus.transmission = null;
                            roomStatus.updateDatabase();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            transmissionImage.setImageResource(R.drawable.ic_timestamp_untransmitted);
                            Toast.makeText(context,
                                    R.string.structures_marked_activity_as_untransmitted,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                    return false;
                }
            });
            // Set service button Click
            ImageButton descriptionButton = convertView.findViewById(R.id.noteButton);
            Button serviceButton = convertView.findViewById(R.id.serviceButton);
            serviceButton.setTag(convertView);
            serviceButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View button) {
                    if (! apiData.isValidContract(roomStatus.contractId)) {
                        // Cannot change a disabled contract
                        Toast.makeText(context, R.string.structures_unable_to_use_contract,
                                Toast.LENGTH_SHORT).show();
                    } else if (roomStatus.transmission != null) {
                        // Cannot change an already transmitted activity
                        Toast.makeText(context, R.string.structures_unable_to_change_transmitted_activity,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Update ServiceActivity for room
                        roomStatus.nextService();
                        updateService(roomStatus);
                        roomStatus.updateDatabase();
                        updateRoomView(rowView, roomStatus);
                    }
                }
            });
            // Set service button Long Click
            serviceButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View button) {
                    if (! apiData.isValidContract(roomStatus.contractId)) {
                        // Cannot change a disabled contract
                        Toast.makeText(context, R.string.structures_unable_to_use_contract,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Show contextual menu for services
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.structures_select_a_service_room);
                        // Sort services list
                        List<String> servicesList = new ArrayList<>();
                        List<Long> servicesIdList = new ArrayList<>();
                        SortedSet<Service> sortedServices = new TreeSet<>(apiData.serviceMap.values());
                        for (Service service : sortedServices) {
                            servicesList.add(service.name);
                            servicesIdList.add(service.id);
                        }
                        builder.setItems(servicesList.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                // Get the selected service and update the user interface
                                roomStatus.service = apiData.serviceMap.get(servicesIdList.get(position));
                                roomStatus.prepareForNothing();
                                updateService(roomStatus);
                                roomStatus.updateDatabase();
                                updateRoomView(rowView, roomStatus);
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    // Don't execute the click event
                    return true;
                }
            });
            // Set service description Click
            descriptionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View button) {
                    final EditText descriptionView = new EditText(context);
                    descriptionView.setText(roomStatus.description);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(R.string.description);
                    alertDialogBuilder.setView(descriptionView);
                    alertDialogBuilder.setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (! apiData.isValidContract(roomStatus.contractId)) {
                                        // Cannot change a disabled contract
                                        Toast.makeText(context, R.string.structures_unable_to_use_contract,
                                                Toast.LENGTH_SHORT).show();
                                    } else if (roomStatus.transmission != null) {
                                        // Cannot change an already transmitted activity
                                        Toast.makeText(context,
                                                R.string.structures_unable_to_change_transmitted_activity,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                            roomStatus.description = descriptionView.getText().toString();
                                            roomStatus.updateDatabase();
                                            updateRoomView(rowView, roomStatus);
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // Show the alert dialog
                    alertDialogBuilder.create().show();
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

        private void updateRoomView(View rowView, RoomStatus roomStatus) {
            // Highlight rooms with at least a service
            ImageView servicePresentImage = rowView.findViewById(R.id.servicePresentImage);
            servicePresentImage.setImageResource(
                    roomsEmployeesAssignedList.get(roomStatus.roomId).size() > 0 ?
                            R.drawable.ic_service_present : R.drawable.ic_service_absent);
            // Set room name
            TextView roomView = rowView.findViewById(R.id.roomView);
            roomView.setText(roomStatus.name);
            // Change room background color
            if (roomsEmployeesAssignedList.get(roomStatus.roomId).size() > 1) {
                roomView.setBackgroundColor(context.getResources().getColor(
                        R.color.color_rooms_background_already_assigned));
            } else {
                roomView.setBackground(null);
            }
            // Set room service
            Button serviceButton = rowView.findViewById(R.id.serviceButton);
            serviceButton.setText(roomStatus.getServiceName());
            // Define descriptionButton
            ImageButton descriptionButton = rowView.findViewById(R.id.noteButton);
            if (this.descriptionEnabledDrawable == null) {
                this.descriptionEnabledDrawable = context.getResources().getDrawable(R.drawable.ic_note);
                this.descriptionDisabledDrawable = Utility.convertDrawableToGrayScale(
                        this.descriptionEnabledDrawable);
            }
            descriptionButton.setEnabled(roomStatus.service != null);
            descriptionButton.setImageDrawable(roomStatus.service == null ?
                    this.descriptionDisabledDrawable : this.descriptionEnabledDrawable);
            // Define transmissionImage
            ImageView transmissionImage = rowView.findViewById(R.id.transmissionImage);
            transmissionImage.setImageResource(roomStatus.transmission == null ?
                    R.drawable.ic_timestamp_untransmitted : R.drawable.ic_timestamp_transmitted);
        }

        public void showAlreadyAssignedEmployees(List<Long> employeesIdList) {
            List<String> employees = new ArrayList<>();
            for (Long employeeId : employeesIdList) {
                Employee employee = this.apiData.employeesMap.get(employeeId);
                employees.add(employee.firstName + " " + employee.lastName);
            }
            // Show contextual menu for services
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.structures_employees_already_assigned);
            builder.setItems(employees.toArray(new String[0]), null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void updateService(RoomStatus roomStatus) {
            // Check if the employee is already assigned for the room
            List<Long> roomAssignationList = roomsEmployeesAssignedList.get(roomStatus.roomId);
            Employee employee = apiData.contractsMap.get(roomStatus.contractId).employee;
            if (roomStatus.service == null) {
                // Un-assign
                roomAssignationList.remove(employee.id);
            } else if (! roomAssignationList.contains(employee.id)) {
                // Assign
                roomAssignationList.add(employee.id);
            }
        }
    }

    private class RoomStatus {
        private final String emptyServiceDescription;
        private final AppDatabase database;
        final String name;
        final long contractId;
        final long roomId;
        final List<Service> services;
        Service service;
        private int serviceCounter;
        String description;
        Date transmission;

        RoomStatus(Context context, String name, long contractId, long roomId,
                   List<Service> services, Service service, String description,
                   Date transmission) {
            this.emptyServiceDescription = context.getString(R.string.empty_service);
            this.database = AppDatabase.getAppDatabase(context);
            this.name = name;
            this.contractId = contractId;
            this.roomId = roomId;
            this.services = services;
            this.service = service;
            this.serviceCounter = this.services.indexOf(this.service);
            this.description = description;
            this.transmission = transmission;
        }

        @SuppressWarnings("UnusedReturnValue")
        Service nextService() {
            // Cycle services
            this.serviceCounter++;
            if (this.serviceCounter == this.services.size()) {
                this.serviceCounter = 0;
            }
            this.service = this.services.get(this.serviceCounter);
            return this.service;
        }

        String getServiceName() {
            // Get current service name
            return this.service == null ? this.emptyServiceDescription : this.service.name;
        }

        void prepareForNothing() {
            // Set the cycle counter to the last element so that the next service will be Nothing
            this.serviceCounter = this.services.size() - 1;
        }

        private void updateDatabase() {
            // Update database row
            new AsyncTask<RoomStatus, Void, Void>() {
                @Override
                protected Void doInBackground(RoomStatus... params) {
                    RoomStatus roomStatus = params[0];
                    List<ServiceActivity> serviceActivityList =
                            database.serviceActivityDao().listByDateContract(
                                    singleton.selectedDate,
                                    roomStatus.contractId, roomStatus.roomId);
                    ServiceActivity serviceActivity;
                    if (serviceActivityList.size() > 0) {
                        serviceActivity = serviceActivityList.get(0);
                        if (roomStatus.service != null) {
                            // Update existing ServiceActivity
                            serviceActivity.serviceId = roomStatus.service.id;
                            serviceActivity.description = roomStatus.description;
                            serviceActivity.transmission = roomStatus.transmission;
                            database.serviceActivityDao().update(serviceActivity);
                            serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                                    serviceActivity);
                        } else {
                            // Delete existing ServiceActivity
                            database.serviceActivityDao().delete(serviceActivity);
                            serviceActivityTable.remove(roomStatus.contractId, roomStatus.roomId);
                        }
                    } else if (roomStatus.service != null) {
                        // Create new ServiceActivity
                        serviceActivity = new ServiceActivity(0,
                                singleton.selectedDate,
                                roomStatus.contractId,
                                roomStatus.roomId,
                                roomStatus.service.id,
                                1, roomStatus.description, null);
                        database.serviceActivityDao().insert(serviceActivity);
                        serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                                serviceActivity);
                    }
                    return null;
                }
            }.execute(this);
        }
    }

    private class EmployeeStatus {
        private final Contract contract;
        private final Date date;
        private final List<TimestampDirection> timestampDirections;
        private final String[] directionsArray;
        private final boolean[] directionsCheckedArray;
        private final AppDatabase database;

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
            this.database = AppDatabase.getAppDatabase(context);
        }

        private void updateDatabase() {
            // Update database row
            new AsyncTask<EmployeeStatus, Void, Void>() {
                @Override
                protected Void doInBackground(EmployeeStatus... params) {
                    EmployeeStatus employeeStatus = params[0];
                    List<Timestamp> timestampsEmployee =
                            database.timestampDao().listByContractNotEnterExit(date, contract.id);
                    // Delete any previous timestamp
                    database.timestampDao().delete(timestampsEmployee.toArray(new Timestamp[0]));
                    // Re-add every active timestamp
                    timestampsEmployee.clear();
                    for (int index = 0; index < employeeStatus.timestampDirections.size(); index++) {
                        if (employeeStatus.directionsCheckedArray[index]) {
                            TimestampDirection timestampDirection = employeeStatus.timestampDirections.get(index);
                            timestampsEmployee.add(new Timestamp(0, employeeStatus.contract.id,
                                    timestampDirection.id, date,"", null));
                        }
                    }
                    database.timestampDao().insert(timestampsEmployee.toArray(new Timestamp[0]));
                    return null;
                }
            }.execute(this);
        }
    }
}
