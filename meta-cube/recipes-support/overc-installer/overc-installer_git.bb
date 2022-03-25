SUMMARY = "OverC installer script"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV ?= "4dfffc0829e839633fa672632ad2befacc327f4b"
PV = "1.0+git${SRCPV}"

SRC_URI = " \
    git://github.com/OverC/overc-installer.git;branch=master;protocol=https \
    file://git/COPYING \
"

S = "${WORKDIR}/git"

do_install() {
	mkdir -p ${D}/opt/${BPN}/
	mkdir -p ${D}${systemd_system_unitdir}/
	install -m755 ${S}/sbin/overc-ctl ${D}/opt/${BPN}/
	install -m755 ${S}/sbin/cubename ${D}/opt/${BPN}/
	install -d ${D}/${sbindir}
	lnr ${D}/opt/${BPN}/overc-ctl ${D}/${sbindir}/overc-ctl
	lnr ${D}/opt/${BPN}/cubename ${D}/${sbindir}/cubename
	install -m 0755 ${S}/files/overc_cleanup.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "/opt/${BPN} \
		${systemd_system_unitdir} \
	    "
RDEPENDS:${PN} += "bash"
