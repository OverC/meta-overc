SUMMARY = "Distributed version control system manpages"
SECTION = "console/utils"
LICENSE = "GPLv2"
DEPENDS = "git"

RDEPENDS_${PN} = "git"
SRC_URI = "https://www.kernel.org/pub/software/scm/git/git-manpages-${PV}.tar.gz"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI[md5sum] = "a7f83828fcffa8089dfb210df43b5e60"
SRC_URI[sha256sum] = "8a905a433807ea94e8cd1887f98c7f0d5145c05fa153c93138083532a05a9b63"

FILES_${PN}-doc = "${mandir}"

do_configure () {
}

do_compile () {
}

do_install () {
	mkdir -p ${D}/${mandir}
	cp -a ${WORKDIR}/man[1-9] ${D}/${mandir}
}
