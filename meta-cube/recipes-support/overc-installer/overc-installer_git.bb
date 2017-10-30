SUMMARY = "OverC installer script"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/OverC/overc-installer.git;branch=master-oci \
    file://git/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
	mkdir -p ${D}/opt/${BPN}/
	mkdir -p ${D}${systemd_system_unitdir}/
	install -m755 ${S}/sbin/overc-ctl ${D}/opt/${BPN}/
	install -d ${D}/${sbindir}
	lnr ${D}/opt/${BPN}/overc-ctl ${D}/${sbindir}/overc-ctl
	install -m 0755 ${S}/files/overc_cleanup.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} += "/opt/${BPN} \
		${systemd_system_unitdir} \
	    "
RDEPENDS_${PN} += "bash"
