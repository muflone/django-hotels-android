package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

public class ReportActivityDetail {
    @ColumnInfo(name = "company_id")
    public long companyId;

    @ColumnInfo(name = "company")
    public String company;

    @ColumnInfo(name = "employee_id")
    public long employeeId;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "contract_id")
    public long contractId;

    @ColumnInfo(name = "datetime")
    public Date datetime;

    @ColumnInfo(name = "building_id")
    public long buildingId;

    @ColumnInfo(name = "building")
    public String building;

    @ColumnInfo(name = "room_id")
    public long roomId;

    @ColumnInfo(name = "room")
    public String room;

    @ColumnInfo(name = "service_id")
    public long serviceId;

    @ColumnInfo(name = "service")
    public String service;

    @ColumnInfo(name = "description")
    public String description;
}
