File Downloader
===============

[![Build Status](https://travis-ci.org/chris-geek/file-downloader.svg?branch=master)](https://travis-ci.org/chris-geek/file-downloader)

This is a derivative work of https://github.com/phudekar/file-downloader.
The goal is to create a file download library with the following features:

- ability to resume interrupted downloads
- optional MD5 integrity checking of the downloaded file, including support for MD5-ETags, such as AWS CloudFront/S3 
- bandwidth throttling
- optional auto-retry mode to manage interrupted connections
- correctly handle HTTP 301/302 status codes (moved permanently or temporary redirects) 

The library comes with a simple command line utility to download a file from a url. 
You can pause the active download by entering `p` and resume it by entering `r`.
You can shutdown the application by entering `q` and resume the download running the application again.

If there exists a file with same name on output path, it will replace that file.

## PRE-REQUISITES

- JDK 1.8

## Running tests
```
gradle test

```

## Build the application
```
gradle build

```

This will create a distributable bundle of the application under `build/libs`.
Moreover, it will generate a jar file `build/distributions/file-downloader-lib-*.*-SNAPSHOT.jar`.
This jar file does not include the sample application, thus it is suitable to be added to your
project in order to use file-downloader as an external library.

### Extract the bundle

```
tar -xvf build/distributions/file-downloader-1.0-SNAPSHOT.tar

cd file-downloader-1.0-SNAPSHOT/bin/

```

## Run the example application

    cd bin
    java samples.Demo [-md5 {<md5hash> | etag}] [ -b <bufferSizeBytes>] [ -t <throttleMs>]  "<http-url>" <location>
    java samples.DemoAutoRetry [-md5 {<md5hash> | etag}] [ -b <bufferSizeBytes>] [ -t <throttleMs>]  "<http-url>" <location>

`samples.DemoAutoRetry` shows the auto-retry feature. You can download a large file and temporary disable you network card
or disconnecting the network cable/turning wifi off.

`samples.DemoResumeInterruptedDownload` shows how the download is resumed if the application exists during download.
By default it will exit after 3 seconds, but you can edit the source code and use a different time.  

Feel free to hack the demo applications to test different features.
    
## Using as a library

You can download the latest jar from the Releases tab on GitHub and add `file-download-lib-<version>.jar` to your classpath.
The simplest way to use file downloader is the following:

    HttpDownloader downloader = new HttpDownloader();
    DownloadEntry entry = new DownloadEntry("http://www-us.apache.org/dist//httpd/httpd-2.2.34.tar.gz", "/tmp");
    downloader.download(entry);
    
In the above example we provide a HTTP(s) URL (http://www-us.apache.org/dist//httpd/httpd-2.2.34.tar.gz) and
a local path. The local path can be either a directory or file. If it is a directory, the original file name will
be used for the downloaded file.

### Progress Notifications

You can receive progress notifications for all events that happen during the download.
Just implement the `ProgressListener` interface and define the methods that you want to implement.
Note that for each method there is an empty default implementation, thus you do not need to implement
all of them. As an example, look at the `ConsoleOutput` class that just prints messages on stdout.

### Auto Retry and Partial Downloads

By default, the downloader will fail in case of interrupted connection. The partially downloaded file
is placed in the target path with the '.part' (partial) extension. The next time you invoke the
`download()` method, the download will automatically resume, unless you remove the `.part` file.

You can also enable auto-retry. In such a case, the downloader will sleep and try automatic reconnect
to restart the partially downloaded file.
In order to enable auto-retry you must create a `DownloadAutoRetryConfig` instance and set it to the
downloader:

    DownloadAutoRetryConfig autoRetryConfig = new DownloadAutoRetryConfig();
    downloader.setAutoRetryConfig(autoRetryConfig); 
    downloader.download(entry);

The `DownloadAutoRetryConfig` comes with some default options that define the sleep time, how the sleep
time is increased at each attempt, maximum connection attempts and so on. You can override the default
configuration as follows:

    DownloadAutoRetryConfig autoRetryConfig = new DownloadAutoRetryConfig();
    
    autoRetryConfig.setInitialDelayTimeMs(2000);    // default is 1000
    autoRetryConfig.setDelayTimeIncrementsMs(1500); // default is 1000
    autoRetryConfig.setMaxDelayTimeMs(120000);      // default is 60000            
    autoRetryConfig.setRetryDelayExponent(2);       // default is 1     
    autoRetryConfig.setMaxRetryAttempts(100);       // default is 0 = unlimited
    
    downloader.setAutoRetryConfig(autoRetryConfig); 
    downloader.download(entry);

### MD5 Checksum

Optionally it is possible to set the MD5 checksum of the downloaded file to check for file integrity.
If the checksum is provided, the downloader will automatically calculate the the checksum of the 
downloaded file and compare with the provided one. If they do not match, an error is raised.

    entry.setFileMd5("33C2543E6D337D6BBF50F18CFB318CE7");
    downloader.download(entry);
  
Since some CDNs, such as AWS CloudFront, provide the MD5 as ETag header, it is also possible to provide
a special value `etag` so that the downloader uses the ETag value as MD5 hash for the file in order to
check for file integrity: 

    entry.setFileMd5("etag");
    downloader.download(entry);

### Bandwidth Throttling

You can control the download bandwidth through two settings on the `HttpDownloader` instance:
   
    HttpDownloader downloader = new HttpDownloader();
    DownloadEntry entry = new DownloadEntry("http://www-us.apache.org/dist//httpd/httpd-2.2.34.tar.gz", "/tmp");
    downloader.setBufferSize(256);
    downloader.setThrottleChunksMs(500);
    downloader.download(entry);
    
In the above example, we have set the download buffer size to 256 bytes (instead of the default, 4096 bytes) and
set a delay of 500 milliseconds for every downloaded buffer.
If you are downloading large files, you might consider using a larger buffer.

### Other

The downloader will always overwrite the local file. Thus, it is responsibility of the client application to check,
before downloading, that there is not another file with the same name on the local disk.

