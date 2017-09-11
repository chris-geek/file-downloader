package com.github.phudekar.downloader;

import com.github.phudekar.downloader.exceptions.InvalidUrlException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpDownloader implements Downloader {
    private int bufferSize = 4096;
    private int throttleChunksMs = 0;
	private List<ProgressListener> progressListeners = new ArrayList<>();

    @Override
    public void download(DownloadEntry entry) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(entry.getUrl()).openConnection();
            long totalBytesRead = entry.getFile().length();
            String rangeHeader = getRangeHeader(totalBytesRead);
			connection.setRequestProperty("Range", rangeHeader);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {

                long totalSize = totalBytesRead + connection.getContentLengthLong();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(entry.getFile(), true);

                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) > -1 && !Thread.currentThread().isInterrupted()) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                    totalBytesRead += bytesRead;
                    this.notifyProgress(entry, totalBytesRead, totalSize);
                    
                    if (this.throttleChunksMs > 0) {
                    	try {
							Thread.sleep(this.throttleChunksMs);
						} catch (InterruptedException e) {
							System.out.println("INTERRUPTED");
						}
                    }
                }

                connection.disconnect();
                outputStream.close();
                
                entry.renamePartialToCompleteDownload();
                
            } else {
                throw new ConnectException("Could not connect. status code : " + responseCode);
            }
        } catch (MalformedURLException e) {
            throw new InvalidUrlException(entry.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRangeHeader(long offset) {
        return "bytes=" + offset + "-";
    }

    private void notifyProgress(DownloadEntry entry, long sizeDownloaded, long totalSize) {
        entry.updateStatus(new DownloadStatus(totalSize, sizeDownloaded));
        this.progressListeners.stream().forEach(progressListener -> progressListener.onProgress(entry));
    }

    public void subscribeForNotification(ProgressListener progressListener) {
        this.progressListeners.add(progressListener);
    }
    
    public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public int getThrottleChunksMs() {
		return throttleChunksMs;
	}

	public void setThrottleChunksMs(int throttleChunksMs) {
		this.throttleChunksMs = throttleChunksMs;
	}

}
