SUMMARY = "OverC support utilities"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/OverC/overc-installer.git;branch=master \
    file://source/cube-cmd \
    file://source/cube-ctl \
    file://source/cube \
    file://source/cube-console \
    file://source/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
    # TODO: add overc-cctl here, instead of overc-installer package

    install -d ${D}${bindir}
    install -d ${D}${sbindir}

    # cubename comes from overc-installer.git
    install -m755 ${S}/sbin/cubename ${D}${bindir}

    # The rest are local utilities
    install -m755 ${WORKDIR}/source/cube-console ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-ctl ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube-cmd ${D}${sbindir}
    install -m755 ${WORKDIR}/source/cube ${D}${sbindir}
}

FILES_${PN} += "/opt/${BPN} \
               ${bindir} ${sbindir}"
RDEPENDS_${PN} += "bash socat"
