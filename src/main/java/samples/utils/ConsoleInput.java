/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */
package samples.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.phudekar.downloader.DownloadEntry;
import com.github.phudekar.downloader.DownloadManager;

public class ConsoleInput {

    public static final char PAUSE = 'p';
    public static final char RESUME = 'r';
    public static final char QUIT = 'q';

    public void listenForCommands(DownloadEntry entry, DownloadManager downloadManager, Runnable onComplete) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            char[] buffer = new char[1];

            while (!entry.isDownloadCompletedOrSuspended()) {
                if (reader.ready() && reader.read(buffer) > 0) {
                    char action = buffer[0];
                    
                    if (action == PAUSE && !downloadManager.isPaused(entry)) {
                        downloadManager.pause(entry);
                        System.out.println("Enter '" + RESUME + "' to resume.");
                    } else if (action == RESUME && downloadManager.isPaused(entry)) {
                        downloadManager.resume(entry);
                        clearLine();
                    } else if (action == QUIT) {
                    	System.out.println("Safely shutting down");
                    	
//                    	if (!downloadManager.isPaused(entry)) {
//                            downloadManager.pause(entry);                    		                    		
//                    	}
                    	
                    	entry.setForceStop(true);
                    }
                }
            }

            reader.close();
            onComplete.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLine() {
        System.out.print("\033[2K"); // clear line
        for (int i = 0; i < 3; i++) {
            System.out.print(String.format("\033[%dA", 1)); // move line up
            System.out.print("\033[2K");
        }
    }
}
