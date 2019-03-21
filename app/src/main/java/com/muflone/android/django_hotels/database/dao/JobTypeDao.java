package com.muflone.android.django_hotels.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.muflone.android.django_hotels.database.models.JobType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface JobTypeDao {
    @Query("SELECT * " +
           "FROM job_types")
    List<JobType> getAll();

    @Query("SELECT * " +
           "FROM job_types " +
           "WHERE id = :id")
    JobType findById(long id);

    @Query("SELECT * " +
           "FROM job_types " +
           "WHERE name = :name")
    JobType findByName(String name);

    @Query("SELECT COUNT(*) " +
           "FROM job_types")
    long count();

    @Insert(onConflict = IGNORE)
    long insert(JobType item);

    @Insert(onConflict = IGNORE)
    void insert(JobType... items);

    @Delete
    void delete(JobType item);

    @Delete
    void delete(JobType... items);

    @Query("DELETE FROM job_types")
    void truncate();
}