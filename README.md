File Downloader
===============

[![Build Status](https://travis-ci.org/chris-geek/file-downloader.svg?branch=master)](https://travis-ci.org/chris-geek/file-downloader)

This is a derivative work of https://github.com/phudekar/file-downloader.
The goal is to create a file download library with the following features:

- ability to resume interrupted downloads
- optional MD5 integrity checking of the downloaded file, including support for MD5-ETags, such as AWS CloudFront/S3 
- bandwidth throttling

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

This will create a distributable bundle of the application.

### Extract the bundle

```
tar -xvf build/distributions/file-downloader-1.0-SNAPSHOT.tar

cd file-downloader-1.0-SNAPSHOT/bin/

```


## Run the application


    ./file-downloader [-md5 {<md5hash> | etag}] [ -b <bufferSizeBytes>] [ -t <throttleMs>]  "<http-url>" <location>

