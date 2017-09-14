package com.github.phudekar.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.github.phudekar.downloader.exceptions.InvalidUrlException;
import com.github.phudekar.downloader.utils.IntegrityChecker;

public class HttpDownloader implements Downloader {
    private int bufferSize = 4096;
    private int throttleChunksMs = 0;
	private List<ProgressListener> progressListeners = new ArrayList<>();
	
	private final static Logger log = Logger.getLogger(HttpDownloader.class.getName());

    @Override
    public void download(DownloadEntry entry) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(entry.getUrl()).openConnection();
            long totalBytesRead = entry.getFile().length();
            String rangeHeader = getRangeHeader(totalBytesRead);
			connection.setRequestProperty("Range", rangeHeader);		

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
            	
            	if (entry.getFileMd5() != null && entry.getFileMd5().equalsIgnoreCase("etag")) {
            		getMd5FromEtag(entry, connection);
            	}
            	
                long totalSize = totalBytesRead + connection.getContentLengthLong();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(entry.getFile(), true);

                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                
                while ((bytesRead = inputStream.read(buffer)) > -1 && !Thread.currentThread().isInterrupted() && !entry.isForceStop()) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                    totalBytesRead += bytesRead;
                    this.notifyProgress(entry, totalBytesRead, totalSize);
                    
                    if (this.throttleChunksMs > 0) {
                    	try {
							Thread.sleep(this.throttleChunksMs);
						} catch (InterruptedException e) {
							log.info("Thread interrupted while sleeping");
						}
                    }
                }
                
                connection.disconnect();
                outputStream.close();
                // Needed to rename the file to remove the .part suffix
                this.notifyProgress(entry, totalBytesRead, totalSize);

                boolean completed = totalSize > 0 && totalBytesRead >= totalSize;
                
                if (entry.getFileMd5() != null && completed /* !Thread.currentThread().isInterrupted() */) {
                	log.info("Calculating md5 signature...");
                	String fileMd5 = new IntegrityChecker().getFileMd5(entry.getLocation());
                	
                	if (!entry.getFileMd5().equalsIgnoreCase(fileMd5)) {
                		throw new StreamCorruptedException("Downloaded and provided MD5 do not match ("+ fileMd5.toUpperCase() + ", "+ entry.getFileMd5().toUpperCase());
                	} else {
                		log.info("MD5 signature is OK");
                	}
                }
                                
            	entry.setDownloadCompletedOrSuspended(true);                
                
            } else {
                throw new ConnectException("Could not connect. status code : " + responseCode);
            }
        } catch (MalformedURLException e) {
            throw new InvalidUrlException(entry.getUrl());
        } catch (IOException e) {
        	// TODO
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			// TODO
			e.printStackTrace();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
    }

	private void getMd5FromEtag(DownloadEntry entry, HttpURLConnection connection) throws Exception {
		String etag = connection.getHeaderField("ETag");
		
		if (etag == null) {
			throw new Exception("Missing ETag header field");
		} else if (etag.startsWith("\"")) {		// eg. AWS S3/CloudFront return the MD5 value in double quotes 
			etag = etag.substring(1, etag.length()-1);
		}
		
		entry.setFileMd5(etag);
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
