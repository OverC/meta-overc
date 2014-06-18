DESCRIPTION = "AsciiDoc is a text document format for writing short documents, articles, books and UNIX man pages."
HOMEPAGE = "http://www.methods.co.nz/asciidoc/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
PR = "r1"

SRC_URI = "http://sourceforge.net/projects/asciidoc/files/asciidoc/8.6.9/asciidoc-8.6.9.tar.gz"

inherit distutils-base autotools pkgconfig autotools-brokensep

export vimdir = "${D}${sysconfdir}/vim"
export DESTDIR = "${D}"

#do_install() {
#	sed -i -e s:/etc/vim::g ${S}/Makefile
#	oe_runmake -e install
#}

FILES_${PN} += "${sysconfdir}"

# TODO: only depend on codecs, csv, doctest, fnmatch, getopt, HTMLParser, locale, optparse, os, re, shutil, StringIO, subprocess, sys, tempfile, time, traceback, urlparse, zipfile
RDEPENDS_${PN} += "python-modules"

SRC_URI[md5sum] = "c59018f105be8d022714b826b0be130a"
SRC_URI[sha256sum] = "78db9d0567c8ab6570a6eff7ffdf84eadd91f2dfc0a92a2d0105d323cab4e1f0"
