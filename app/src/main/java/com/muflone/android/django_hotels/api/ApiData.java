package com.muflone.android.django_hotels.api;

import android.annotation.SuppressLint;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Brand;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;
import com.muflone.android.django_hotels.database.models.Company;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.JobType;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Service;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ApiData {
    public final HashMap<Long, Brand> brandsMap;
    public final HashMap<Long, Building> buildingsMap;
    public final HashMap<Long, Command> commandsMap;
    public final HashMap<Long, CommandUsage> commandsUsageMap;
    public final HashMap<Long, Company> companiesMap;
    public final HashMap<Long, Contract> contractsMap;
    public final HashMap<String, Contract> contractsGuidMap;
    public final HashMap<Long, ContractType> contractTypeMap;
    public final HashMap<Long, Employee> employeesMap;
    public final HashMap<Long, JobType> jobTypesMap;
    public final HashMap<Long, Room> roomsMap;
    public final HashMap<Long, Structure> roomsStructureMap;
    public final HashMap<Long, Building> roomsBuildingMap;
    public final HashMap<Long, Service> serviceMap;
    public final HashMap<Long, Service> serviceExtraMap;
    public final HashMap<Long, Structure> structuresMap;
    public final HashMap<Long, TimestampDirection> timestampDirectionsMap;
    public TimestampDirection enterDirection;
    public TimestampDirection exitDirection;
    public List<TimestampDirection> timestampDirectionsNotEnterExit;
    public Exception exception;

    @SuppressLint("UseSparseArrays")
    public ApiData() {
        this.brandsMap = new HashMap<>();
        this.buildingsMap = new HashMap<>();
        this.commandsMap = new HashMap<>();
        this.commandsUsageMap = new HashMap<>();
        this.companiesMap = new HashMap<>();
        this.contractsMap = new HashMap<>();
        this.contractsGuidMap = new HashMap<>();
        this.contractTypeMap = new HashMap<>();
        this.employeesMap = new HashMap<>();
        this.jobTypesMap = new HashMap<>();
        this.roomsMap = new HashMap<>();
        this.roomsStructureMap = new HashMap<>();
        this.roomsBuildingMap = new HashMap<>();
        this.serviceMap = new HashMap<>();
        this.serviceExtraMap = new HashMap<>();
        this.structuresMap = new HashMap<>();
        this.timestampDirectionsMap = new HashMap<>();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidContract(Long contractId) {
        Contract contract = Objects.requireNonNull(this.contractsMap.get(contractId));
        return contract.enabled &&
                Singleton.getInstance().selectedDate.compareTo(contract.startDate) >= 0 &&
                Singleton.getInstance().selectedDate.compareTo(contract.endDate) <= 0;
    }

    public ArrayList<Command> getCommandsByContext(String contextType) {
        /*
         * Get commands list for the ContextType specified
         */
        ArrayList<Command> results = new ArrayList<>();
        for (Command command : this.commandsMap.values()) {
            // Process only the commands in the current context
            if (command.context.equals(contextType)) {
                results.add(command);
            }
        }
        // Sort results by id
        Collections.sort(results);
        return results;
    }
}
