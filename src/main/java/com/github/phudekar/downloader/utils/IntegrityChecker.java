/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package com.github.phudekar.downloader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IntegrityChecker {
	public String getFileMd5(String path) throws NoSuchAlgorithmException, IOException {
	
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte bytes[] = new byte[16836];
		
		try (InputStream is = Files.newInputStream(Paths.get(path));				
				DigestInputStream dis = new DigestInputStream(is, md)) {
			/* Read decorated stream (dis) to EOF as normal... */
			while (dis.read(bytes) > 0 ) {}
		}
		
		byte[] digest = md.digest();

	    StringBuilder sb = new StringBuilder();
	    
	    for (byte b : digest) {
	        sb.append(String.format("%02X", b));
	    }
	    
	    return sb.toString();
	}
}
