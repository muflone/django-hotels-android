package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(tableName = "activities",
        indices = {
                @Index(value = {"date", "contract_id", "room_id"}, unique = true),
        })
public class ServiceActivity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "date")
    @NonNull
    public final Date date;

    @ColumnInfo(name = "contract_id", index = true)
    public final long contractId;

    @ColumnInfo(name = "structure_id")
    public final long structureId;

    @ColumnInfo(name = "room_id", index = true)
    public final long roomId;

    @ColumnInfo(name = "service_id", index = true)
    public long serviceId;

    @ColumnInfo(name = "service_qty")
    public long serviceQty;

    @ColumnInfo(name = "description")
    @NonNull
    public String description;

    @ColumnInfo(name = "transmission", index = true)
    @Nullable
    public Date transmission;

    public ServiceActivity(long id, @NonNull Date date, long contractId,
                           long structureId, long roomId,
                           long serviceId, long serviceQty,
                           @NonNull String description, @Nullable Date transmission) {
        this.id = id;
        this.date = date;
        this.contractId = contractId;
        this.structureId = structureId;
        this.roomId = roomId;
        this.serviceId = serviceId;
        this.serviceQty = serviceQty;
        this.description = description;
        this.transmission = transmission;
    }
}
