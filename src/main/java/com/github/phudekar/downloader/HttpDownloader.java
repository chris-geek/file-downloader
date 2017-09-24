/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

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

import com.github.phudekar.downloader.exceptions.EtagNotFoundException;
import com.github.phudekar.downloader.exceptions.InvalidUrlException;
import com.github.phudekar.downloader.exceptions.UnexpectedResponseException;
import com.github.phudekar.downloader.utils.IntegrityChecker;

public class HttpDownloader implements Downloader {
    private int bufferSize = 4096;
    private int throttleChunksMs = 0;
	private List<ProgressListener> progressListeners = new ArrayList<>();
	private DownloadAutoRetryConfig autoRetryConfig = null;
	
	private final static Logger log = Logger.getLogger(HttpDownloader.class.getName());
	
    @Override
    public void download(DownloadEntry entry)  {
    	boolean retryDownload;
    	
    	do {
    		retryDownload = false;
    		
	        try {
	            downloadFromUrl(entry, entry.getUrl());
	        } catch (UnexpectedResponseException e) {
	            if (e.getResponseCode() == 302 || e.getResponseCode() == 301) {
	            	// received a moved permanently or temporary redirect. e.getLocation() contains the new URL. We try again.
	                try {
	                    log.info("Received 302. Trying again with " + e.getLocation());
	                    downloadFromUrl(entry, e.getLocation());
	                } catch (UnexpectedResponseException e1) {
	                    this.notifyError("Could not download file from : " + e.getLocation());
	                } catch (NoSuchAlgorithmException | EtagNotFoundException e1) {
	                	this.notifyError(e1.getClass().getSimpleName() + ": " + e.getMessage());                	
					} catch (IOException ex) {
						this.notifyError(e.getClass().getSimpleName() + ": " + e.getMessage());
	    				retryDownload = this.shouldRetryDownload();
	    			}
	            } else {
	            	this.notifyError("Could not download file. Received respose code: " + e.getResponseCode());
	            }
	        } catch (NoSuchAlgorithmException | EtagNotFoundException e) {
	        	this.notifyError(e.getClass().getSimpleName() + ": " + e.getMessage());
			} catch (IOException e) {
				this.notifyError(e.getClass().getSimpleName() + ": " + e.getMessage());
				retryDownload = this.shouldRetryDownload();
			}
    	} while (retryDownload);
    }

    
    /**
     * 
     * @param entry
     * @param url
     * @throws UnexpectedResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws EtagNotFoundException
     */
    private void downloadFromUrl(DownloadEntry entry, String url) throws UnexpectedResponseException, IOException, NoSuchAlgorithmException, EtagNotFoundException {
        HttpURLConnection connection = null;
        FileOutputStream outputStream = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            long totalBytesRead = entry.getFile().length();
            connection.setRequestProperty("Range", getRangeHeader(totalBytesRead));
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {

            	if (entry.getFileMd5() != null && entry.getFileMd5().equalsIgnoreCase("etag")) {
            		getMd5FromEtag(entry, connection);
            	}
            	
                long totalSize = totalBytesRead + connection.getContentLengthLong();
                InputStream inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(entry.getFile(), true);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                this.resetAutoRetryCounter();
                
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
                connection = null;
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
                	
                	this.notifyCompleted();
                }
                                
            	entry.setDownloadCompletedOrSuspended(true);            	

            } else {
                throw new UnexpectedResponseException(responseCode, connection.getHeaderField("Location"));
            }                	
        } catch (MalformedURLException e) {
            throw new InvalidUrlException(entry.getUrl());
        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();

            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                	log.info(e.getMessage());
                }
        }
    }

    /**
     * 
     * @param entry
     * @param connection
     * @throws EtagNotFoundException
     */
	private void getMd5FromEtag(DownloadEntry entry, HttpURLConnection connection) throws EtagNotFoundException  {
		String etag = connection.getHeaderField("ETag");
		
		if (etag == null) {
			throw new EtagNotFoundException();
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
    
    private void notifyError(String msg) {
    	this.progressListeners.stream().forEach(progressListener -> progressListener.onError(msg));
    }
    
    private void notifyCompleted() {
    	this.progressListeners.stream().forEach(progressListener -> progressListener.onCompleted());
    }
    
    private void notifySleepBeforeAutoRetry(long millisec) {
    	this.progressListeners.stream().forEach(progressListener -> progressListener.onSleepBeforeAutoRetry(millisec));
    }
    
    private void notifyOnAutoRetryAttempt() {
    	this.progressListeners.stream().forEach(progressListener -> progressListener.onAutoRetryAttempt());
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


	public DownloadAutoRetryConfig getAutoRetryConfig() {
		return autoRetryConfig;
	}

	public void setAutoRetryConfig(DownloadAutoRetryConfig autoRetryConfig) {
		this.autoRetryConfig = autoRetryConfig;
	}
	
	/**
	 * If auto-retry has been enabled, sleeps and returns true.
	 * The sleep time is calculated by the DownloadAutoRetryConfig class according to its configuration.
	 * 
	 * @return true if auto-retry has been enabled
	 */
	protected boolean shouldRetryDownload() {
		if (this.autoRetryConfig != null) {
			try {
				long delayTimeMs = this.autoRetryConfig.calculateCurrentDelayTime();
				this.notifySleepBeforeAutoRetry(delayTimeMs);
				Thread.sleep(delayTimeMs); 
			} catch (InterruptedException e) {
				return false;
			}			
			this.notifyOnAutoRetryAttempt();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Invoked once the download has been resumed so that, in case of another connection failure,
	 * the retry delay starts from the initial value again.
	 */
	protected void resetAutoRetryCounter() {
		if (this.autoRetryConfig != null) {
			this.autoRetryConfig.resetAutoRetryCounter();
		}
	}
}
