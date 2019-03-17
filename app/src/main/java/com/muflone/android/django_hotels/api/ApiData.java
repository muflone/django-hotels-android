package com.muflone.android.django_hotels.api;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.ArrayList;
import java.util.List;

public class ApiData {
    public final List<Structure> structures;
    public final List<Contract> contracts;
    public Exception exception;

    public ApiData() {
        this.structures = new ArrayList<Structure>();
        this.contracts = new ArrayList<Contract>();
    }
}
