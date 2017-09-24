/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package samples;

import com.github.phudekar.downloader.Command;
import com.github.phudekar.downloader.ConsoleInput;
import com.github.phudekar.downloader.ConsoleOutput;
import com.github.phudekar.downloader.DownloadAutoRetryConfig;
import com.github.phudekar.downloader.DownloadEntry;
import com.github.phudekar.downloader.HttpDownloader;
import com.github.phudekar.downloader.exceptions.InvalidCommandException;
import com.github.phudekar.downloader.utils.CommandParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class shows how to setup the auto-retry feature. 
 *
 */
public class DemoAutoRetry {

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

            DownloadAutoRetryConfig autoRetryConfig = new DownloadAutoRetryConfig();
            autoRetryConfig.setInitialDelayTimeMs(1000);
            autoRetryConfig.setDelayTimeIncrementsMs(1000);
            autoRetryConfig.setMaxDelayTimeMs(60000);            
            autoRetryConfig.setRetryDelayExponent(2);            
            autoRetryConfig.setMaxRetryAttempts(100);
            
            downloader.setAutoRetryConfig(autoRetryConfig);
            
            System.out.println("Starting download. Disconnect and reconnect your network cable during download to test auto-retry");
            
        	downloader.download(entry);
			
        } catch (InvalidCommandException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
