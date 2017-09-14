package com.github.phudekar.downloader.utils;

import com.github.phudekar.downloader.Command;
import com.github.phudekar.downloader.exceptions.InvalidCommandException;

public class CommandParser {


    private static final String DEFAULT_LOCATION = ".";

    public Command parse(String[] args) throws InvalidCommandException {
        if (args.length == 0) {
            throw new InvalidCommandException("usage: [-b <bufferSize>] [-t <throttleTimeMs>] [-md5 {<md5hash> | etag}] <url>");
        }
        
        int i = 0;
        int throttle = 0;
        int bufferSize = 0;
        String md5 = null;
        
        while (true) {
	        if (args[i].equals("-t")) {
	        	throttle = Integer.parseInt(args[i+1]);
	        	i += 2;
	        	continue;
	        }
	        
	        if (args[i].equals("-b")) {
	        	bufferSize = Integer.parseInt(args[i+1]);
	        	i += 2;
	        	continue;
	        }
	        
	        if (args[i].equals("-md5")) {
	        	md5 = args[i+1];
	        	i += 2;
	        	continue;
	        }
	        
	        break;
        }

        String url = args[i].trim();                       
        String location = DEFAULT_LOCATION;
        i++;
        
        if (args.length > i) {
            location = args[i].trim();
        }
        return new Command(url, location, throttle, bufferSize, md5);
    }
}
