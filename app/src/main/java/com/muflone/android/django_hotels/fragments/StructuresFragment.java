package com.muflone.android.django_hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.muflone.android.django_hotels.ContractViewsUpdater;
import com.muflone.android.django_hotels.EmployeeStatus;
import com.muflone.android.django_hotels.EmployeeViewsUpdater;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.RoomStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractBuildings;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.tasks.TaskStructureLoadEmployees;

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
    private EmployeeViewsUpdater employeeViewsUpdater;
    private ContractViewsUpdater contractViewsUpdater;
    private ExpandableListView roomsView;
    private ExpandableListAdapter buildingRoomsAdapter;
    private final List<String> employeesList = new ArrayList<>();
    private final List<EmployeeStatus> employeesStatusList = new ArrayList<>();
    private final List<String> buildingsList = new ArrayList<>();
    private final HashMap<String, List<RoomStatus>> roomsList = new HashMap<>();
    private final List<Service> roomServicesList = new ArrayList<>();
    private final HashMap<String, Boolean> buildingsClosedStatusMap = new HashMap<>();
    private static final Table<Long, Long, ServiceActivity> serviceActivityTable = HashBasedTable.create();
    @SuppressLint("UseSparseArrays")
    private static final HashMap<Long, List<Long>> roomsEmployeesAssignedList = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START STRUCTURE BEGIN commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_STRUCTURE_BEGIN);

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
        // Execute START STRUCTURE END commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_STRUCTURE_END);
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
        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.roomsView = rootLayout.findViewById(R.id.roomsView);
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
        this.employeesStatusList.clear();
        roomsEmployeesAssignedList.clear();
        serviceActivityTable.clear();
        // Initialize buildings groups to collapsed
        this.buildingsClosedStatusMap.clear();
        for (Building building : this.singleton.selectedStructure.buildings) {
            this.buildingsClosedStatusMap.put(building.name,
                    this.api.settings.getBuildingsInitiallyClosed());
            // Initialize empty already assigned rooms list (<Room ID><List of employees>)
            for (Room room : building.rooms) {
                roomsEmployeesAssignedList.put(room.id, new ArrayList<>());
            }
        }
        new TaskStructureLoadEmployees(this.employeesList, this.employeesView,
                serviceActivityTable, this.employeesStatusList,
                roomsEmployeesAssignedList).execute();
    }

    private void loadEmployee(Employee employee) {
        // Get the first contract for the employee
        Contract contract = Objects.requireNonNull(this.apiData.contractsMap.get(employee.contractBuildings.get(0).contractId));
        // Update Employee and Contract details
        this.employeeViewsUpdater.updateViews(employee);
        this.contractViewsUpdater.updateViews(contract);
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
                    if (serviceActivityTable.contains(contractBuilding.contractId, room.id)) {
                        service = apiData.serviceMap.get(
                                serviceActivityTable.get(contractBuilding.contractId, room.id).serviceId);
                        description = serviceActivityTable.get(contractBuilding.contractId, room.id).description;
                        transmission = serviceActivityTable.get(contractBuilding.contractId, room.id).transmission;
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

    private static class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Context context;
        private final List<String> buildingsList;
        private final HashMap<String, List<RoomStatus>> roomsList;
        private final ApiData apiData;
        private final Singleton singleton = Singleton.getInstance();
        private final Drawable descriptionEnabledDrawable;
        private final Drawable descriptionDisabledDrawable;

        private static class ViewHolder {
            // Views holder caching items for ExpandableListAdapter
            // https://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
            ImageView servicePresentImage;
            TextView roomView;
            Button serviceButton;
            ImageButton descriptionButton;
            ImageView transmissionImage;
        }

        ExpandableListAdapter(Context context, List<String> listDataHeader,
                              HashMap<String, List<RoomStatus>> listChildData) {
            this.context = context;
            this.buildingsList = listDataHeader;
            this.roomsList = listChildData;
            this.apiData = this.singleton.apiData;
            this.descriptionEnabledDrawable = context.getResources().getDrawable(R.drawable.ic_note);
            this.descriptionDisabledDrawable = Utility.convertDrawableToGrayScale(
                    this.descriptionEnabledDrawable);
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
            ViewHolder viewHolder;
            RoomStatus roomStatus = getChild(groupPosition, childPosition);
            if (convertView == null) {
                // Get a new ViewHolder
                LayoutInflater inflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.structures_building_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.servicePresentImage = convertView.findViewById(R.id.servicePresentImage);
                viewHolder.roomView = convertView.findViewById(R.id.roomView);
                viewHolder.transmissionImage = convertView.findViewById(R.id.transmissionImage);
                viewHolder.descriptionButton = convertView.findViewById(R.id.noteButton);
                viewHolder.serviceButton = convertView.findViewById(R.id.serviceButton);
                convertView.setTag(viewHolder);
            } else {
                // Get the ViewHolder from the saved instance in the ConvertView
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Update database row
            roomStatus.updateDatabase(serviceActivityTable);
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
                roomStatus.updateDatabase(serviceActivityTable);
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
                    roomStatus.updateDatabase(serviceActivityTable);
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
                        roomStatus.updateDatabase(serviceActivityTable);
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
                                    roomStatus.updateDatabase(serviceActivityTable);
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

        private void updateRoomView(RoomStatus roomStatus, ViewHolder viewHolder) {
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
            viewHolder.descriptionButton.setEnabled(roomStatus.service != null);
            viewHolder.descriptionButton.setImageDrawable(roomStatus.service == null ?
                    this.descriptionDisabledDrawable : this.descriptionEnabledDrawable);
            // Define transmissionImage
            viewHolder.transmissionImage.setImageResource(roomStatus.transmission == null ?
                    R.drawable.ic_timestamp_untransmitted : R.drawable.ic_timestamp_transmitted);
        }

        @SuppressWarnings("WeakerAccess")
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

        @SuppressWarnings("WeakerAccess")
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
}
