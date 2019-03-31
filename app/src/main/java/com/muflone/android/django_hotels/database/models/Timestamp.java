package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(tableName = "timestamps",
        indices = {
                @Index(value = {"contract_id", "direction_id", "datetime"}, unique = true),
        })
public class Timestamp {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "contract_id", index = true)
    public final long contractId;

    @ColumnInfo(name = "direction_id", index = true)
    @NonNull
    public final String directionId;

    @ColumnInfo(name = "datetime")
    @NonNull
    public final Date datetime;

    @ColumnInfo(name = "description")
    @NonNull
    public final String description;

    @ColumnInfo(name = "transmission", index = true)
    @Nullable
    public Date transmission;

    public Timestamp(long id, long contractId, @NonNull String directionId, Date datetime, @NonNull String description, @Nullable Date transmission) {
        this.id = id;
        this.contractId = contractId;
        this.directionId = directionId;
        this.datetime = datetime;
        this.description = description;
        this.transmission = transmission;
    }
}
