/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package samples;

import com.github.phudekar.downloader.DownloadEntry;
import com.github.phudekar.downloader.HttpDownloader;
import com.github.phudekar.downloader.exceptions.InvalidCommandException;
import com.github.phudekar.downloader.utils.CommandParser;

import samples.utils.Command;
import samples.utils.ConsoleInput;
import samples.utils.ConsoleOutput;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is an example of download that can be completely stopped and resumed at the next run. 
 * Aborts the download after 3 seconds.
 * Run this class multiple times passing a URL that takes more than 3 seconds to download.
 * Alternatively, set SHUTDOWN_AFTER_MS to a lower time.
 *
 */
public class DemoResumeInterruptedDownload {
	private static long SHUTDOWN_AFTER_MS = 3000;

    public static void main(String[] args) {

        HttpDownloader downloader = new HttpDownloader();        
        CommandParser commandParser = new CommandParser();
        ConsoleOutput output = new ConsoleOutput();       

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

            DownloadEntry entry = new DownloadEntry(command.getUrl(), command.getLocation());
            
            if (command.getMd5() != null) {
            	System.out.println("MD5: "+ command.getMd5());
            	entry.setFileMd5(command.getMd5());
            }            
            
            Thread thread = new Thread(() -> {
            	downloader.download(entry);
            });
			thread.start();
			
			try { Thread.sleep(SHUTDOWN_AFTER_MS); } catch (InterruptedException e) { e.printStackTrace(); }
			entry.setForceStop(true);			
			thread.join();
			
        } catch (InvalidCommandException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
			e.printStackTrace();
		} 
    }

}
