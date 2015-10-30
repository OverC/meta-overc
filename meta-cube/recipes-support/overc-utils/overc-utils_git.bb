SUMMARY = "OverC support utilities"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/WindRiver-OpenSourceLabs/overc-installer.git;branch=master \
    file://git/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
    # TODO: add overc-cctl here, instead of overc-installer package

    install -d ${D}${bindir}
    install -m755 ${S}/sbin/cubename ${D}${bindir}
}

FILES_${PN} += "/opt/${BPN} \
               ${bindir}"
RDEPENDS_${PN} += "bash"
