/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package samples;

import com.github.phudekar.downloader.Command;
import com.github.phudekar.downloader.ConsoleInput;
import com.github.phudekar.downloader.ConsoleOutput;
import com.github.phudekar.downloader.DownloadEntry;
import com.github.phudekar.downloader.DownloadManager;
import com.github.phudekar.downloader.HttpDownloader;
import com.github.phudekar.downloader.exceptions.InvalidCommandException;
import com.github.phudekar.downloader.utils.CommandParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    public static void main(String[] args) {

        HttpDownloader downloader = new HttpDownloader();        
        CommandParser commandParser = new CommandParser();
        ConsoleOutput output = new ConsoleOutput();
        ConsoleInput input = new ConsoleInput();

        downloader.subscribeForNotification(output);

        try {
            Command command = commandParser.parse(args);
            System.out.println("\nDownloading from " + command.getUrl());
            
            if (command.getThrottleMs() > 0) {
            	System.out.println("Throttling ms: "+ command.getThrottleMs());
            	downloader.setThrottleChunksMs(command.getThrottleMs());
            }
            
            if (command.getBufferSize() > 0) {
            	System.out.println("Buffer size: "+ command.getBufferSize());
            	downloader.setBufferSize(command.getBufferSize());
            }
            

            ExecutorService executor = Executors.newCachedThreadPool();
            DownloadManager downloadManager = new DownloadManager(downloader, executor);

            DownloadEntry entry = new DownloadEntry(command.getUrl(), command.getLocation());
            
            if (command.getMd5() != null) {
            	System.out.println("MD5: "+ command.getMd5());
            	entry.setFileMd5(command.getMd5());
            }
            
            downloadManager.download(entry);
            input.listenForCommands(entry, downloadManager, () -> {
                if (!executor.isShutdown()) executor.shutdownNow();
            });

        } catch (InvalidCommandException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
