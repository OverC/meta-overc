SUMMARY = "Distributed version control system manpages"
SECTION = "console/utils"
LICENSE = "GPLv2"
DEPENDS = "git"

RDEPENDS_${PN} = "git"
SRC_URI = "https://www.kernel.org/pub/software/scm/git/git-manpages-${PV}.tar.gz"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI[md5sum] = "b5ddd262b608804ba4403f0f82d881d7"
SRC_URI[sha256sum] = "69dcb3decdb33dd35491935e80f71c40c576b536df4223eb98d5f7ccd9643293"

FILES_${PN}-doc = "${mandir}"

do_configure () {
}

do_compile () {
}

do_install () {
	mkdir -p ${D}/${mandir}
	cp -a ${WORKDIR}/man[1-9] ${D}/${mandir}
}
