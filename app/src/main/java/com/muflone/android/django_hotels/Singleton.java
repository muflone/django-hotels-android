package com.muflone.android.django_hotels;

import android.content.Context;

import com.muflone.android.django_hotels.api.Api;

import java.io.Serializable;

public class Singleton implements Serializable {
    private static volatile Singleton instance;
    public Api api;
    public Settings settings;

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

    // Make singleton from serialize and deserialize operation.
    protected Singleton readResolve() {
        return getInstance();
    }
}
