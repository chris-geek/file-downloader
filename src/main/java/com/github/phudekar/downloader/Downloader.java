package com.github.phudekar.downloader;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.github.phudekar.downloader.exceptions.EtagNotFoundException;

public interface Downloader {

    void download(DownloadEntry entry);

    void subscribeForNotification(ProgressListener progressListener);

}
