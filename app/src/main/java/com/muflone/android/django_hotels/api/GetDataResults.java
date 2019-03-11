package com.muflone.android.django_hotels.api;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.ArrayList;
import java.util.List;

public class GetDataResults {
    public final List<Structure> structures;
    public final List<Contract> contracts;

    public GetDataResults() {
        this.structures = new ArrayList<Structure>();
        this.contracts = new ArrayList<Contract>();
    }
}
