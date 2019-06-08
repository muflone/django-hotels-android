package com.muflone.android.django_hotels.api.exceptions;

public class InvalidServerStatusException extends Exception {
    // Exception used for invalid server status response
    public InvalidServerStatusException(String message) {
        super(message);
    }
}
