package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "contracts",
        indices = {
            @Index(value = {"guid"}, unique = true),
        },
        foreignKeys = {
            @ForeignKey(entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "employee_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "company_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = ContractType.class,
                        parentColumns = "id",
                        childColumns = "contract_type_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = JobType.class,
                        parentColumns = "id",
                        childColumns = "job_type_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Contract {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "guid")
    public final String guid;

    @ColumnInfo(name = "start")
    public final Date startDate;

    @ColumnInfo(name = "end")
    public final Date endDate;

    @ColumnInfo(name = "enabled")
    public final boolean enabled;

    @ColumnInfo(name = "active")
    public final boolean active;

    @Ignore
    public Employee employee;

    @ColumnInfo(name = "employee_id", index = true)
    public final long employeeId;

    @Ignore
    public Company company;

    @ColumnInfo(name = "company_id", index = true)
    public final long companyId;

    @Ignore
    public ContractType contractType;

    @ColumnInfo(name = "contract_type_id", index = true)
    public final long contractTypeId;

    @Ignore
    public JobType jobType;

    @ColumnInfo(name = "job_type_id", index = true)
    public final long jobTypeId;

    @Ignore
    public List<Long> buildings;

    public Contract(long id, String guid, Date startDate, Date endDate,
                    boolean enabled, boolean active,
                    long employeeId, long companyId, long contractTypeId,
                    long jobTypeId) {
        this.id = id;
        this.guid = guid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enabled = enabled;
        this.active = active;
        this.employeeId = employeeId;
        this.companyId = companyId;
        this.contractTypeId = contractTypeId;
        this.jobTypeId = jobTypeId;
    }

    @Ignore
    public Contract(long id, String guid, Date startDate, Date endDate,
                    boolean enabled, boolean active,
                    Employee employee, Company company, ContractType contractType,
                    JobType jobType, List<Long> buildings) {
        this(id, guid, startDate, endDate, enabled, active, employee.id, company.id,
                contractType.id, jobType.id);
        this.employee = employee;
        this.company = company;
        this.contractType = contractType;
        this.jobType = jobType;
        this.buildings = buildings;
    }

    @Ignore
    public Contract(JSONObject jsonObject) throws JSONException, ParseException {
        this(jsonObject.getJSONObject("contract").getLong("id"),
                jsonObject.getJSONObject("contract").getString("guid"),
                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getJSONObject("contract").getString("start")),
                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getJSONObject("contract").getString("end")),
                jsonObject.getJSONObject("contract").getBoolean("enabled"),
                jsonObject.getJSONObject("contract").getBoolean("active"),
                new Employee(jsonObject.getJSONObject("employee")),
                new Company(jsonObject.getJSONObject("company")),
                new ContractType(jsonObject.getJSONObject("type")),
                new JobType(jsonObject.getJSONObject("job")),
                new ArrayList<>());
        // Loop every building id
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        for (int i = 0; i < jsonBuildings.length(); i++) {
            this.buildings.add(jsonBuildings.getLong(i));
        }
    }
}
