/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = "contract_buildings",
        primaryKeys = {"contract_id", "building_id"},
        foreignKeys = {
                @ForeignKey(entity = Contract.class,
                        parentColumns = "id",
                        childColumns = "contract_id",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Building.class,
                        parentColumns = "id",
                        childColumns = "building_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class ContractBuildings {
    @ColumnInfo(name = "contract_id", index = true)
    public final long contractId;

    @ColumnInfo(name = "building_id", index = true)
    public final long buildingId;

    public ContractBuildings(final long contractId, final long buildingId) {
        this.contractId = contractId;
        this.buildingId = buildingId;
    }
}
