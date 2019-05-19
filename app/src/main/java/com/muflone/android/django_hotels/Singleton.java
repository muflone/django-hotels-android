package com.muflone.android.django_hotels;

import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Structure;

import java.io.Serializable;
import java.util.Date;

public class Singleton implements Serializable {
    private static volatile Singleton instance;
    public Api api;
    public ApiData apiData;
    public Settings settings;
    public Date selectedDate;
    public Structure selectedStructure;

    private Singleton() {
        // Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
