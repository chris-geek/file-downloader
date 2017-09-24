/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader;

import java.util.logging.Logger;

public class DownloadAutoRetryConfig {
	private long initialDelayTimeMs = 1000; // default
	private long maxDelayTimeMs = 60000; // default
	private int delayTimeIncrementsMs = 1000; // default
	private int currentRetryCount = 0;
	private float retryDelayExponent = 1.0F; // default
	private int maxRetryAttempts = 0; // default (0 = unlimited)
	
	private final static Logger log = Logger.getLogger(HttpDownloader.class.getName());
	
	public DownloadAutoRetryConfig() {		
	}
	
	public DownloadAutoRetryConfig(long initialDelayTimeMillisec, long maxDelayTimeMillisec, int delayTimeIncrementsMillsec, float retryDelayExponent) {
		this.initialDelayTimeMs = initialDelayTimeMillisec;
		this.maxDelayTimeMs = maxDelayTimeMillisec;
		this.delayTimeIncrementsMs = delayTimeIncrementsMillsec;
		this.retryDelayExponent = retryDelayExponent;
	}
	
	protected boolean hasReachedMaxRetryAttempts() {
		if (this.maxRetryAttempts == 0) {
			return false;
		} else {
			return this.currentRetryCount >= this.maxRetryAttempts;
		}
	}
	
	protected long calculateCurrentDelayTime() {
		long currentDelayMillisec = (long) (this.initialDelayTimeMs + this.delayTimeIncrementsMs * Math.pow(this.currentRetryCount++, this.retryDelayExponent));
		
		if (currentDelayMillisec >= this.maxDelayTimeMs) {
			currentDelayMillisec = this.maxDelayTimeMs;
		}
		
		return currentDelayMillisec;		
	}

	public void resetAutoRetryCounter() {
		this.currentRetryCount = 0;		
	}

	public long getInitialDelayTimeMs() {
		return initialDelayTimeMs;
	}

	public void setInitialDelayTimeMs(long initialDelayTimeMs) {
		this.initialDelayTimeMs = initialDelayTimeMs;
	}

	public long getMaxDelayTimeMs() {
		return maxDelayTimeMs;
	}

	public void setMaxDelayTimeMs(long maxDelayTimeMs) {
		this.maxDelayTimeMs = maxDelayTimeMs;
	}

	public int getDelayTimeIncrementsMs() {
		return delayTimeIncrementsMs;
	}

	public void setDelayTimeIncrementsMs(int delayTimeIncrementsMs) {
		this.delayTimeIncrementsMs = delayTimeIncrementsMs;
	}

	public float getRetryDelayExponent() {
		return retryDelayExponent;
	}

	public void setRetryDelayExponent(float retryIncrementFactor) {
		this.retryDelayExponent = retryIncrementFactor;
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempts;
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
		this.maxRetryAttempts = maxRetryAttempts;
	}
	
}
