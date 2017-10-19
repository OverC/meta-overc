SUMMARY = "nanomsg is a socket library that provides several common \
communication patterns."
DESCRIPTION = "The nanomsg library is a simple high-performance \
implementation of several scalability protocols. These scalability \
protocols are light-weight messaging protocols which can be used to \
solve a number of very common messaging patterns, such as request/reply, \
publish/subscribe, surveyor/respondent, and so forth. These protocols \
can run over a variety of transports such as TCP, UNIX sockets, and even \
WebSocket."
HOMEPAGE = "http://nanomsg.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=587b3fd7fd291e418ff4d2b8f3904755"

SRC_URI = " \
	   git://github.com/nanomsg/nanomsg.git \
           file://0001-nanocat-add-stdin-processing.patch \
           file://0001-nanocat-add-EOF-transmission-for-faster-exit.patch \
           file://0001-nanocat-improve-raw-unbuffered-mode.patch \
	  "

SRCREV = "7e12a20e038234060d41d03c20721d08117f8607"
PV = "1.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
