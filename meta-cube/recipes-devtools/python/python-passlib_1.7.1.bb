DESCRIPTION = "comprehensive password hashing framework supporting over 30 schemes"
HOMEPAGE = "http://passlib.googlecode.com"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73eed1a5470b969951dac411086c7036"

SRCNAME = "passlib"

SRC_URI = "https://pypi.python.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "254869dae3fd9f09f0746a3cb29a0b15"
SRC_URI[sha256sum] = "3d948f64138c25633613f303bcc471126eae67c04d5e3f6b7b8ce6242f8653e0"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
