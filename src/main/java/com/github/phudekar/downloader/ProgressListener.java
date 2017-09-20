package com.github.phudekar.downloader;

public interface ProgressListener {

    void onProgress(DownloadEntry entry);
    
    void onError(String msg);
    
    void onCompleted();

}
