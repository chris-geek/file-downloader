package com.github.phudekar.downloader;

import com.github.phudekar.downloader.utils.FileNameParser;

import java.io.File;
import java.util.Optional;

public class DownloadEntry {

    private static final String PARTIAL_DOWNLOAD_SUFFIX = ".part";
	static final String DEFAULT_FILE_NAME = "download";
    private final String url;
    private String location;
    private final File file;
    private DownloadStatus status = new DownloadStatus(0, 0);

    public DownloadEntry(String url, String location) {
        this.url = url;
        this.location = location;
        File locationFile = new File(location);
        
		if (locationFile.isDirectory()) {
			this.location = locationFile.getAbsolutePath();
            this.location = locationFile + File.separator + getFileName(url);
        }		
		
        this.file = new File(this.location + PARTIAL_DOWNLOAD_SUFFIX);        
    }
    
    public void renamePartialToCompleteDownload() {
    	this.file.renameTo(new File(this.location));
    }
    
    private String getFileName(String url) {
        Optional<String> fileName = FileNameParser.getFileName(url);
        return fileName.isPresent() ? fileName.get() : DEFAULT_FILE_NAME;
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }

    public String getLocation() {
        return location;
    }    

    public DownloadStatus getStatus() {
        return status;
    }

    public void updateStatus(DownloadStatus status) {
        this.status = status;
        if (this.status.isComplete()) {
            this.file.renameTo(new File(this.location));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadEntry that = (DownloadEntry) o;

        return url.equals(that.url) && location.equals(that.location);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}
