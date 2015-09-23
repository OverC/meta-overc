SUMMARY = "OverC installer script"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "ed0c8a1d0cd5427a1bfb969af3f4a51644920cec"
SRC_URI = " \
    git://github.com/WindRiver-OpenSourceLabs/overc-installer.git \
    file://git/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
	mkdir -p ${D}/opt/${BPN}/
	install -m755 ${S}/sbin/overc-cctl ${D}/opt/${BPN}/
}

FILES_${PN} += "/opt/${BPN}"
RDEPENDS_${PN} += "bash"
