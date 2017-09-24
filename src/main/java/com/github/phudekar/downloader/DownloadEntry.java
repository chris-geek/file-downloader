/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader;

import com.github.phudekar.downloader.utils.FileNameParser;

import java.io.File;
import java.util.Optional;

public class DownloadEntry {

    public static final String PARTIAL_DOWNLOAD_SUFFIX = ".part";
	static final String DEFAULT_FILE_NAME = "download";
    private final String url;
    private String location;
    private final File file;
    private DownloadStatus status = new DownloadStatus(0, 0);
    private String fileMd5 = null;	// optional. If provided, checks file integrity. Can be set to "etag" if the HTTP server provides an MD5 ETag.
    private boolean forceStop = false;
    private boolean downloadCompletedOrSuspended = false;

	public DownloadEntry(String url, String location) {
        this.url = url;
        this.location = location;
        File locationFile = new File(location);
        
		if (locationFile.isDirectory()) {
			this.location = locationFile.getAbsolutePath();
            this.location = locationFile + File.separator + getFileName(url);
        }
		
        this.file = new File(this.location + PARTIAL_DOWNLOAD_SUFFIX);
        
        // this.file.delete();	NO! Otherwise we cannot resume previous downloads
        new File(this.location).delete();	// just delete the target file, if existing
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
        this.renameFileIfComplete();
    }

	public void renameFileIfComplete() {
		if (this.status.isComplete()) {
            this.file.renameTo(new File(this.location));
        }
	}
	
    public boolean isForceStop() {
		return forceStop;
	}

	public void setForceStop(boolean forceStop) {
		this.forceStop = forceStop;
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

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public boolean isDownloadCompletedOrSuspended() {
		return downloadCompletedOrSuspended;
	}

	public void setDownloadCompletedOrSuspended(boolean downloadCompleted) {
		this.downloadCompletedOrSuspended = downloadCompleted;
	}
}
