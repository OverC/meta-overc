SUMMARY = "CUBE Command Server"
DESCRIPTION = "A service for taking action on commands from containers"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://cube-cmd-server.service \
           file://cube-cmd-server.conf \
           file://auth.db \
           file://functions.d \
"
RDEPENDS:${PN} = "bash gawk dtach overc-utils nanomsg"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "cube-cmd-server.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/cube-cmd-server.service ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/cube-cmd-server.conf ${D}${sysconfdir}/

    install -d ${D}${localstatedir}/lib/cube-cmd-server/functions.d/
    cp -r ${WORKDIR}/functions.d/* ${D}${localstatedir}/lib/cube-cmd-server/functions.d/
    install -m 0644 ${WORKDIR}/auth.db ${D}${localstatedir}/lib/cube-cmd-server/
}

FILES:${PN} = "${sysconfdir}/ \
               ${localstatedir}/lib/cube-cmd-server/ \
	       ${systemd_system_unitdir}/cube-cmd-server.service"
