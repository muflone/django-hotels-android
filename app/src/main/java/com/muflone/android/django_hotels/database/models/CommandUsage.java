package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "commands_usage")
public class CommandUsage {
    @PrimaryKey
    public final Long id;

    @ColumnInfo(name = "used")
    public int used;

    public CommandUsage(Long id, int used) {
        this.id = id;
        this.used = used;
    }
}