package com.muflone.android.django_hotels.api;

import com.muflone.android.django_hotels.database.models.Brand;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Company;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractType;
import com.muflone.android.django_hotels.database.models.Employee;
import com.muflone.android.django_hotels.database.models.JobType;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.HashMap;

public class ApiData {
    public HashMap<Long, Brand> brandsMap;
    public HashMap<Long, Building> buildindsMap;
    public HashMap<Long, Company> companiesMap;
    public HashMap<Long, Contract> contractsMap;
    public HashMap<Long, ContractType> contractTypeMap;
    public HashMap<Long, Employee> employeesMap;
    public HashMap<Long, JobType> jobTypesMap;
    public HashMap<Long, Room> roomsMap;
    public HashMap<Long, Structure> structuresMap;
    public Exception exception;

    public ApiData() {
        this.brandsMap = new HashMap<>();
        this.buildindsMap = new HashMap<>();
        this.companiesMap = new HashMap<>();
        this.contractsMap = new HashMap<>();
        this.contractTypeMap = new HashMap<>();
        this.employeesMap = new HashMap<>();
        this.jobTypesMap = new HashMap<>();
        this.roomsMap = new HashMap<>();
        this.structuresMap = new HashMap<>();
    }
}
