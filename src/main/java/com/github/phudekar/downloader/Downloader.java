/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader;

public interface Downloader {

    void download(DownloadEntry entry);

    void subscribeForNotification(ProgressListener progressListener);
    
    DownloadAutoRetryConfig getAutoRetryConfig();

}
