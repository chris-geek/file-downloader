package com.github.phudekar.downloader.exceptions;

public class UnexpectedResponseException extends Throwable {
    private final String location;
    private final int responseCode;

    public UnexpectedResponseException(int responseCode, String location) {
        super("Could not connect. status code : " + responseCode);
        this.responseCode = responseCode;
        this.location = location;
    }

    public int getResponseCode(){
      return this.responseCode;
    }

    public String getLocation() {
        return location;
    }
}