package com.github.phudekar.downloader.utils;

import com.github.phudekar.downloader.Command;
import com.github.phudekar.downloader.exceptions.InvalidCommandException;

public class CommandParser {


    private static final String DEFAULT_LOCATION = ".";

    public Command parse(String[] args) throws InvalidCommandException {
        if (args.length == 0) {
            throw new InvalidCommandException("URL Missing from arguments");
        }
        
        int i = 0;
        int throttle = 0;
        int bufferSize = 0;
        
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
	        
	        break;
        }

        String url = args[i].trim();                       
        String location = DEFAULT_LOCATION;
        i++;
        
        if (args.length > i) {
            location = args[i].trim();
        }
        return new Command(url, location, throttle, bufferSize);
    }
}
