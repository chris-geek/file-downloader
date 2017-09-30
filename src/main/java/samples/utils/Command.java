/**
 * This file is released under the MIT license (https://opensource.org/licenses/MIT)
 * as defined in the file 'LICENSE', which is part of this source code package.
 */

package samples.utils;

public class Command {

    private String url;
    private String location;
    private int throttleMs = 0;
    private int bufferSize = 0;
    private String md5 = null;

    public Command(String url, String location, int throttleMs, int bufferSize, String md5) {

        this.url = url;
        this.location = location;
        this.throttleMs = throttleMs;
        this.bufferSize = bufferSize;
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

	public int getThrottleMs() {
		return throttleMs;
	}

	public void setThrottleMs(int throttleMs) {
		this.throttleMs = throttleMs;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
