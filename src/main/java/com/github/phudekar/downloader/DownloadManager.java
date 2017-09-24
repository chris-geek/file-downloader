/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DownloadManager {

    ExecutorService executor;

    final HashMap<DownloadEntry, Future> downloads = new HashMap<>();
    private Downloader downloader;


    public DownloadManager(Downloader downloader,ExecutorService executor) {
        this.downloader = downloader;
        this.executor = executor;
    }

    public void download(DownloadEntry entry) {
        if (!downloads.containsKey(entry))
            downloads.put(entry, executor.submit(() -> { downloader.download(entry); }));
    }

    public Set<DownloadEntry> getDownloads() {
        return this.downloads.keySet();
    }

    public void pause(DownloadEntry entry) {
        if (this.downloads.containsKey(entry))
            this.downloads.get(entry).cancel(true);
    }

    public void resume(DownloadEntry entry) {
        if (downloads.containsKey(entry))
            downloads.put(entry, executor.submit(() -> downloader.download(entry)));
    }

    public boolean isPaused(DownloadEntry entry) {
        return this.downloads.containsKey(entry) && this.downloads.get(entry).isCancelled();
    }

}
