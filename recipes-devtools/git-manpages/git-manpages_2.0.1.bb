SUMMARY = "Distributed version control system manpages"
SECTION = "console/utils"
LICENSE = "GPLv2"
DEPENDS = "git"

RDEPENDS_${PN} = "git"
SRC_URI = "https://www.kernel.org/pub/software/scm/git/git-manpages-${PV}.tar.gz"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI[md5sum] = "b36a03d806207ebd38913fcc4e8053a6"
SRC_URI[sha256sum] = "a8b2af48319a197c2359db9b4f01448827dd9a796e68510e162faee66ae11052"

FILES_${PN}-doc = "${mandir}"

do_configure () {
}

do_compile () {
}

do_install () {
	mkdir -p ${D}/${mandir}
	cp -a ${WORKDIR}/man[1-9] ${D}/${mandir}
}
