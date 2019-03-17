package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface ContractDao {
    @Query("SELECT * FROM contracts")
    List<Contract> getAll();

    @Query("SELECT * FROM contracts WHERE id = :id")
    Contract findById(long id);

    @Query("SELECT * FROM contracts WHERE guid = :guid")
    Contract findByGuid(String guid);

    @Query("SELECT COUNT(*) FROM contracts")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(Contract item);

    @Insert(onConflict = IGNORE)
    void insert(Contract... items);

    @Delete
    void delete(Contract item);

    @Delete
    void delete(Contract... items);

    @Query("DELETE FROM contracts")
    void truncate();
}