package com.github.phudekar.downloader;

public interface ProgressListener {

    default void onProgress(DownloadEntry entry) {}
    
    default void onError(String msg) {}
    
    default void onCompleted() {}

}
