package com.github.phudekar.downloader;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class DownloadManagerTests {

    final String fileName = "README.md";
    String url = "https://raw.githubusercontent.com/phudekar/file-downloader/master/" + fileName;
    final String location = ".";
    private DownloadManager downloadManager;

    @Rule
    public WireMockRule service = new WireMockRule(8081);
    private Downloader downloader;
    private ExecutorService executor;

    @Before
    public void beforeEach() {
        downloader = mock(Downloader.class);
        executor = Executors.newCachedThreadPool();
        downloadManager = new DownloadManager(downloader, executor);
    }

    @Test
    public void shouldCreateDownloadEntryForFile() {
        DownloadEntry downloadEntry = new DownloadEntry(url, location);
        downloadManager.download(downloadEntry);

        assertThat(downloadEntry.getUrl(), is(url));
        assertThat(downloadEntry.getFile().getPath(), is(location + File.separatorChar + fileName + DownloadEntry.PARTIAL_DOWNLOAD_SUFFIX));
    }

    @Test
    public void shouldReturnDownloadEntries() {
        DownloadEntry downloadEntry = new DownloadEntry(url, location);
        downloadManager.download(downloadEntry);

        assertThat(downloadManager.getDownloads().size(), is(1));
        assertThat(downloadManager.getDownloads().stream().findFirst().get(), is(downloadEntry));
    }

    @After
    public void afterEach() {
        if (!executor.isShutdown())
            executor.shutdownNow();
    }

}
