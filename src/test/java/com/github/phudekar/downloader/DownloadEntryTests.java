package com.github.phudekar.downloader;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

public class DownloadEntryTests {

    @Test
    public void shouldSetDefaultFilenameForDownloadEntry() {
        String url = "https://raw.githubusercontent.com/phudekar/file-downloader/master/";
        String location = "." + File.separatorChar + "bin";

        DownloadEntry downloadEntry = new DownloadEntry(url, location);
        assertThat(downloadEntry.getFile().getPath(), is(location + File.separatorChar + DownloadEntry.DEFAULT_FILE_NAME + DownloadEntry.PARTIAL_DOWNLOAD_SUFFIX));
    }


}
