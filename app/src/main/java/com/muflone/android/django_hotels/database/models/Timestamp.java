package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "timestamps",
        primaryKeys = {"contract_id", "direction_id"},
        indices = {
                @Index(value = "contract_id", unique = false),
                @Index(value = "direction_id", unique = false)
        },
        foreignKeys = {
                @ForeignKey(entity = Contract.class,
                        parentColumns = "id",
                        childColumns = "contract_id",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = TimestampDirection.class,
                        parentColumns = "id",
                        childColumns = "direction_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Timestamp {
    @ColumnInfo(name = "contract_id")
    public final long contractId;

    @ColumnInfo(name = "direction_id")
    @NonNull
    public final String directionId;

    @ColumnInfo(name = "date")
    public final Date date;

    @ColumnInfo(name = "time")
    public final Date time;

    @ColumnInfo(name = "description")
    @NonNull
    public final String description;

    public Timestamp(long contractId, @NonNull String directionId, Date date, Date time, @NonNull String description) {
        this.contractId = contractId;
        this.directionId = directionId;
        this.date = date;
        this.time = time;
        this.description = description;
    }
}
