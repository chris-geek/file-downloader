package com.github.phudekar.downloader.exceptions;

public class EtagNotFoundException extends RuntimeException {

    public EtagNotFoundException(){
        super("Missing ETag header field");
    }
}
