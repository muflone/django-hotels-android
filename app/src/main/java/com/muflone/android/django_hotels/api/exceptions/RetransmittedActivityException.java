package com.muflone.android.django_hotels.api.exceptions;

public class RetransmittedActivityException extends Exception {
    // Exception used for activities already transmitted and detected from the server
    public RetransmittedActivityException() {
        super();
    }
}
