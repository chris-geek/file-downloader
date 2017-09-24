# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

- [CG 2017-24-09] Added support for auto-retry in case of lost connection.
- [CG 2017-11-09] Added HttpDownloader.set/getBufferSize()
- [CG 2017-11-09] Added HttpDownloader.set/getThrottleChunksMs() for bandwidth control
- [CG 2017-11-09] At the end of the download, the file is renamed removing the .part (partial) suffix
- [CG 2017-11-09] Added -t <throttle> and -b <bufferSize> as optional parameters on the command line 
