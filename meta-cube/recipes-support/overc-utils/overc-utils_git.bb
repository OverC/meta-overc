SUMMARY = "OverC support utilities"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/WindRiver-OpenSourceLabs/overc-installer.git;branch=master \
    file://source/cube-essential \
    file://source/cube-ctl \
    file://source/cube \
    file://source/cube-console \
    file://source/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
    # TODO: add overc-cctl here, instead of overc-installer package

    install -d ${D}${bindir}
    install -m755 ${S}/sbin/cubename ${D}${bindir}

    install -m755 ${WORKDIR}/source/cube-console ${D}${bindir}
}

FILES_${PN} += "/opt/${BPN} \
               ${bindir}"
RDEPENDS_${PN} += "bash socat"
