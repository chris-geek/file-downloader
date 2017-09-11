package com.github.phudekar.downloader;

public class Command {

    private String url;
    private String location;
    private int throttleMs = 0;
    private int bufferSize = 0;

    public Command(String url, String location, int throttleMs, int bufferSize) {

        this.url = url;
        this.location = location;
        this.throttleMs = throttleMs;
        this.bufferSize = bufferSize;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

	public int getThrottleMs() {
		return throttleMs;
	}

	public void setThrottleMs(int throttleMs) {
		this.throttleMs = throttleMs;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
