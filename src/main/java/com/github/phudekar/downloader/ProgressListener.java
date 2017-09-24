/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader;

public interface ProgressListener {

    default void onProgress(DownloadEntry entry) {}
    
    default void onError(String msg) {}
    
    default void onCompleted() {}
    
    default void onSleepBeforeAutoRetry(long millisec) {}
    
    default void onAutoRetryAttempt() {}

}
