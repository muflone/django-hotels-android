package com.muflone.android.django_hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class StructuresFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private final Api api = this.singleton.api;
    private final ApiData apiData = this.singleton.apiData;

    private Context context;
    private View rootLayout;
    private ListView employeesView;
    private ExpandableListView roomsView;
    private ExpandableListAdapter buildingRoomsAdapter;
    private final List<String> employeesList = new ArrayList<>();
    private final List<EmployeeStatus> employeesStatusList = new ArrayList<>();
    private final List<String> buildingsList = new ArrayList<>();
    private final HashMap<String, List<RoomStatus>> roomsList = new HashMap<>();
    private final List<Service> roomServicesList = new ArrayList<>();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable = HashBasedTable.create();
    @SuppressLint("UseSparseArrays")
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
        this.loadUI(inflater, Objects.requireNonNull(container));

        this.employeesView.setAdapter(new ArrayAdapter<>(
                this.context, android.R.layout.simple_list_item_activated_1, this.employeesList));
        this.employeesView.setOnItemClickListener(
                (parent, view, position, id) -> loadEmployee(this.singleton.selectedStructure.employees.get(position)));
        this.employeesView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Show contextual menu for employee
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.structures_employee_set_conditions);
            // Get service directions list
            EmployeeStatus employeeStatus = employeesStatusList.get(position);
            builder.setMultiChoiceItems(employeeStatus.directionsArray, employeeStatus.directionsCheckedArray,
                    (dialogInterface, i, b) -> {
                        // This lambda/listener does nothing
                    });
            // Save choices when the OK button is pressed
            builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> employeeStatus.updateDatabase());
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });

        // Load the employees for the selected structure
        if (this.singleton.selectedStructure != null) {
            this.loadEmployees();
        }

        // Build services list for rooms (the first element is empty)
        this.roomServicesList.add(null);
        for (Service service : this.apiData.serviceMap.values()) {
            // Show only services with show_in_app = true
            if (service.show_in_app) {
                this.roomServicesList.add(service);
            }
        }

        this.buildingRoomsAdapter = new ExpandableListAdapter(this.context, this.buildingsList, this.roomsList);
        this.roomsView.setAdapter(this.buildingRoomsAdapter);
        this.roomsView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            String groupName = parent.getExpandableListAdapter().getGroup(groupPosition).toString();
            buildingsClosedStatusMap.put(groupName, ! Objects.requireNonNull(buildingsClosedStatusMap.get(groupName)));
            Utility.setExpandableListViewHeight(parent, groupPosition,
                    this.api.settings.getRoomsListStandardHeight());
            return false;
        });
        // Execute START STRUCTURE commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                Constants.CONTEXT_START_STRUCTURE);
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

    private void loadEmployees() {
        // Load employees list for the selected Structure tab
        this.employeesList.clear();
        this.employeesStatusList.clear();
        this.roomsEmployeesAssignedList.clear();
        this.serviceActivityTable.clear();
        // Initialize buildings groups to collapsed
        this.buildingsClosedStatusMap.clear();
        for (Building building : this.singleton.selectedStructure.buildings) {
            this.buildingsClosedStatusMap.put(building.name,
                    this.api.settings.getBuildingsInitiallyClosed());
            // Initialize empty already assigned rooms list (<Room ID><List of employees>)
            for (Room room : building.rooms) {
                this.roomsEmployeesAssignedList.put(room.id, new ArrayList<>());
            }
        }
        new StructuresLoadEmployeesTask(this.employeesList, this.employeesView,
                this.serviceActivityTable, this.employeesStatusList,
                this.roomsEmployeesAssignedList).execute();
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
        Contract contract = Objects.requireNonNull(this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId));
        this.contractIdView.setText(String.valueOf(contract.id));
        this.contractCompanyView.setText(Objects.requireNonNull(this.apiData.companiesMap.get(contract.companyId)).name);
        // Add contract info
        ContractType contractType = Objects.requireNonNull(this.apiData.contractTypeMap.get(contract.contractTypeId));
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
            Building building = Objects.requireNonNull(this.apiData.buildingsMap.get(contractBuilding.buildingId));
            // Only show the buildings for the current structure
            if (building.structureId == this.singleton.selectedStructure.id) {
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
            if (Objects.requireNonNull(this.buildingsClosedStatusMap.get(this.buildingsList.get(group)))) {
                this.roomsView.collapseGroup(group);
            } else {
                this.roomsView.expandGroup(group);
            }
        }
        // Allocate space for the expanded list
        Utility.setExpandableListViewHeight(this.roomsView, -1, this.api.settings.getRoomsListStandardHeight());
    }

    private static class StructuresLoadEmployeesTask extends AsyncTask<Void, Void, Void> {
        private final Singleton singleton = Singleton.getInstance();
        private final List<String> employeesList;
        private final WeakReference<ListView> employeesView;
        private final Table<Long, Long, ServiceActivity> serviceActivityTable;
        private final List<EmployeeStatus> employeesStatusList;
        private final HashMap<Long, List<Long>> roomsEmployeesAssignedList;

        public StructuresLoadEmployeesTask(List<String> employeesList,
                                           ListView employeesView,
                                           Table<Long, Long, ServiceActivity> serviceActivityTable,
                                           List<EmployeeStatus> employeesStatusList,
                                           HashMap<Long, List<Long>> roomsEmployeesAssignedList) {
            this.employeesList = employeesList;
            this.employeesView = new WeakReference<>(employeesView);
            this.serviceActivityTable = serviceActivityTable;
            this.employeesStatusList = employeesStatusList;
            this.roomsEmployeesAssignedList = roomsEmployeesAssignedList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Load employees for the selected structure
            for (Employee employee : this.singleton.selectedStructure.employees) {
                this.employeesList.add(String.format("%s %s", employee.firstName, employee.lastName));
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

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Context context;
        private final List<String> buildingsList;
        private final HashMap<String, List<RoomStatus>> roomsList;
        private final ApiData apiData;
        private final Singleton singleton = Singleton.getInstance();
        private Drawable descriptionEnabledDrawable;
        private Drawable descriptionDisabledDrawable;

        ExpandableListAdapter(Context context, List<String> listDataHeader,
                              HashMap<String, List<RoomStatus>> listChildData) {
            this.context = context;
            this.buildingsList = listDataHeader;
            this.roomsList = listChildData;
            this.apiData = this.singleton.apiData;
        }

        @Override
        public RoomStatus getChild(int groupPosition, int childPosition) {
            return Objects.requireNonNull(this.roomsList.get(this.buildingsList.get(groupPosition))).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            StructuresViewHolder viewHolder;
            RoomStatus roomStatus = getChild(groupPosition, childPosition);
            if (convertView == null) {
                // Get a new StructuresViewHolder
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.structures_building_item, parent, false);
                viewHolder = new StructuresViewHolder();
                viewHolder.servicePresentImage = convertView.findViewById(R.id.servicePresentImage);
                viewHolder.roomView = convertView.findViewById(R.id.roomView);
                viewHolder.transmissionImage = convertView.findViewById(R.id.transmissionImage);
                viewHolder.descriptionButton = convertView.findViewById(R.id.noteButton);
                viewHolder.serviceButton = convertView.findViewById(R.id.serviceButton);
                convertView.setTag(viewHolder);
            } else {
                // Get the StructuresViewHolder from the saved instance in the ConvertView
                viewHolder = (StructuresViewHolder) convertView.getTag();
            }
            // Update database row
            roomStatus.updateDatabase();
            // Update view row
            this.updateRoomView(roomStatus, viewHolder);
            // Handle service present image LongClick
            viewHolder.servicePresentImage.setOnLongClickListener(view -> {
                if (Objects.requireNonNull(roomsEmployeesAssignedList.get(roomStatus.roomId)).size() > 0) {
                    showAlreadyAssignedEmployees(roomsEmployeesAssignedList.get(roomStatus.roomId));
                }
                return false;
            });
            // Set transmission LongClick
            viewHolder.transmissionImage.setOnLongClickListener(button -> {
                roomStatus.transmission = null;
                roomStatus.updateDatabase();
                viewHolder.transmissionImage.setImageResource(R.drawable.ic_timestamp_untransmitted);
                Toast.makeText(context,
                        R.string.structures_marked_activity_as_untransmitted,
                        Toast.LENGTH_SHORT).show();
                return false;
            });
            // Set service button Click
            viewHolder.serviceButton.setTag(convertView);
            viewHolder.serviceButton.setOnClickListener(button -> {
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
                    updateRoomView(roomStatus, viewHolder);
                }
            });
            // Set service button Long Click
            viewHolder.serviceButton.setOnLongClickListener(button -> {
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
                    builder.setItems(servicesList.toArray(new String[0]), (dialog, position) -> {
                        // Get the selected service and update the user interface
                        roomStatus.service = apiData.serviceMap.get(servicesIdList.get(position));
                        roomStatus.prepareForNothing();
                        updateService(roomStatus);
                        roomStatus.updateDatabase();
                        updateRoomView(roomStatus, viewHolder);
                        dialog.dismiss();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                // Don't execute the click event
                return true;
            });
            // Set service description Click
            viewHolder.descriptionButton.setOnClickListener(button -> {
                final EditText descriptionView = new EditText(context);
                descriptionView.setText(roomStatus.description);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.description);
                alertDialogBuilder.setView(descriptionView);
                alertDialogBuilder.setCancelable(true)
                        .setPositiveButton(android.R.string.ok, (dialog, id) -> {
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
                                    updateRoomView(roomStatus, viewHolder);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.cancel());

                // Show the alert dialog
                alertDialogBuilder.create().show();
            });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.roomsList.get(this.buildingsList.get(groupPosition)) != null ?
                    Objects.requireNonNull(this.roomsList.get(this.buildingsList.get(groupPosition))).size() :
                    0;
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

        private void updateRoomView(RoomStatus roomStatus, StructuresViewHolder viewHolder) {
            // Highlight rooms with at least a service
            viewHolder.servicePresentImage.setImageResource(
                    Objects.requireNonNull(roomsEmployeesAssignedList.get(roomStatus.roomId)).size() > 0 ?
                            R.drawable.ic_service_present : R.drawable.ic_service_absent);
            // Set room name
            viewHolder.roomView.setText(roomStatus.name);
            // Change room background color
            if (Objects.requireNonNull(roomsEmployeesAssignedList.get(roomStatus.roomId)).size() > 1) {
                viewHolder.roomView.setBackgroundColor(context.getResources().getColor(
                        R.color.color_rooms_background_already_assigned));
            } else {
                viewHolder.roomView.setBackground(null);
            }
            // Set room service
            viewHolder.serviceButton.setText(roomStatus.getServiceName());
            // Define descriptionButton
            if (this.descriptionEnabledDrawable == null) {
                this.descriptionEnabledDrawable = context.getResources().getDrawable(R.drawable.ic_note);
                this.descriptionDisabledDrawable = Utility.convertDrawableToGrayScale(
                        this.descriptionEnabledDrawable);
            }
            viewHolder.descriptionButton.setEnabled(roomStatus.service != null);
            viewHolder.descriptionButton.setImageDrawable(roomStatus.service == null ?
                    this.descriptionDisabledDrawable : this.descriptionEnabledDrawable);
            // Define transmissionImage
            viewHolder.transmissionImage.setImageResource(roomStatus.transmission == null ?
                    R.drawable.ic_timestamp_untransmitted : R.drawable.ic_timestamp_transmitted);
        }

        public void showAlreadyAssignedEmployees(List<Long> employeesIdList) {
            List<String> employees = new ArrayList<>();
            for (Long employeeId : employeesIdList) {
                Employee employee = Objects.requireNonNull(this.apiData.employeesMap.get(employeeId));
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
            List<Long> roomAssignationList = Objects.requireNonNull(roomsEmployeesAssignedList.get(roomStatus.roomId));
            Employee employee = Objects.requireNonNull(apiData.contractsMap.get(roomStatus.contractId)).employee;
            if (roomStatus.service == null) {
                // Un-assign
                roomAssignationList.remove(employee.id);
            } else if (! roomAssignationList.contains(employee.id)) {
                // Assign
                roomAssignationList.add(employee.id);
            }
        }
    }

    private static class StructuresViewHolder {
        // Views holder caching items for ExpandableListAdapter
        // https://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
        ImageView servicePresentImage;
        TextView roomView;
        Button serviceButton;
        ImageButton descriptionButton;
        ImageView transmissionImage;
    }

    private class RoomStatus {
        private final String emptyServiceDescription;
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
            new RoomStatusUpdateDatabaseTask(serviceActivityTable).execute(this);
        }
    }

    private static class RoomStatusUpdateDatabaseTask extends AsyncTask<RoomStatus, Void, Void> {
        private final Singleton singleton = Singleton.getInstance();
        private final Table<Long, Long, ServiceActivity> serviceActivityTable;

        public RoomStatusUpdateDatabaseTask(Table<Long, Long, ServiceActivity> serviceActivityTable) {
            this.serviceActivityTable = serviceActivityTable;
        }

        @Override
        protected Void doInBackground(RoomStatus... params) {
            RoomStatus roomStatus = params[0];
            List<ServiceActivity> serviceActivityList =
                    this.singleton.database.serviceActivityDao().listByDateContract(
                            this.singleton.selectedDate,
                            roomStatus.contractId, roomStatus.roomId);
            ServiceActivity serviceActivity;
            if (serviceActivityList.size() > 0) {
                serviceActivity = serviceActivityList.get(0);
                if (roomStatus.service != null) {
                    // Update existing ServiceActivity
                    serviceActivity.serviceId = roomStatus.service.id;
                    serviceActivity.description = roomStatus.description;
                    serviceActivity.transmission = roomStatus.transmission;
                    this.singleton.database.serviceActivityDao().update(serviceActivity);
                    serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                            serviceActivity);
                } else {
                    // Delete existing ServiceActivity
                    this.singleton.database.serviceActivityDao().delete(serviceActivity);
                    serviceActivityTable.remove(roomStatus.contractId, roomStatus.roomId);
                }
            } else if (roomStatus.service != null) {
                // Create new ServiceActivity
                serviceActivity = new ServiceActivity(0,
                        this.singleton.selectedDate,
                        roomStatus.contractId,
                        roomStatus.roomId,
                        roomStatus.service.id,
                        1, roomStatus.description, null);
                this.singleton.database.serviceActivityDao().insert(serviceActivity);
                serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                        serviceActivity);
            }
            return null;
        }
    }

    private static class EmployeeStatus {
        private final Contract contract;
        private final Date date;
        private final List<TimestampDirection> timestampDirections;
        private final String[] directionsArray;
        private final boolean[] directionsCheckedArray;

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

        private void updateDatabase() {
            // Update database row
            new EmployeeStatusUpdateDatabaseTask().execute(this);
        }
    }

    private static class EmployeeStatusUpdateDatabaseTask extends AsyncTask<EmployeeStatus, Void, Void> {
        private final Singleton singleton = Singleton.getInstance();

        // Update database for EmployeeStatus
        @Override
        protected Void doInBackground(EmployeeStatus... params) {
            EmployeeStatus employeeStatus = params[0];
            List<Timestamp> timestampsEmployee = this.singleton.database.timestampDao().listByContractNotEnterExit(
                    employeeStatus.date, employeeStatus.contract.id);
            // Delete any previous timestamp
            this.singleton.database.timestampDao().delete(timestampsEmployee.toArray(new Timestamp[0]));
            // Re-add every active timestamp
            timestampsEmployee.clear();
            for (int index = 0; index < employeeStatus.timestampDirections.size(); index++) {
                if (employeeStatus.directionsCheckedArray[index]) {
                    TimestampDirection timestampDirection = employeeStatus.timestampDirections.get(index);
                    timestampsEmployee.add(new Timestamp(0, employeeStatus.contract.id,
                            timestampDirection.id, employeeStatus.date,"", null));
                }
            }
            this.singleton.database.timestampDao().insert(timestampsEmployee.toArray(new Timestamp[0]));
            return null;
        }
    }
}
