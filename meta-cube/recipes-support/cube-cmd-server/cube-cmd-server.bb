SUMMARY = "CUBE Command Server"
DESCRIPTION = "A service for taking action on commands from containers"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://cube-cmd-server \
           file://cube-cmd-server.service \
           file://cube-cmd-server.conf \
           file://cube-cmd-server-functions \
"
RDEPENDS_${PN} = "bash socat gawk cube-cmd-server-functions"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "cube-cmd-server.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

PACKAGES += "${PN}-dom0-conf ${PN}-host-conf ${PN}-functions"

do_install_append() {
    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/cube-cmd-server.service ${D}/lib/systemd/system/

    install -d ${D}${base_sbindir}
    install -m 0755 ${WORKDIR}/cube-cmd-server ${D}${base_sbindir}

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/cube-cmd-server.conf ${D}${sysconfdir}/cube-cmd-server.conf-host
    install -m 0644 ${WORKDIR}/cube-cmd-server.conf ${D}${sysconfdir}/cube-cmd-server.conf-dom0
    install -m 0644 ${WORKDIR}/cube-cmd-server-functions ${D}${sysconfdir}/cube-cmd-server-functions
}

FILES_${PN} = "${base_sbindir}/cube-cmd-server lib/systemd/system/cube-cmd-server.service"

FILES_${PN}-dom0-conf = "${sysconfdir}/cube-cmd-server.conf-dom0"
FILES_${PN}-host-conf = "${sysconfdir}/cube-cmd-server.conf-host"
FILES_${PN}-functions = "${sysconfdir}/cube-cmd-server-functions"

pkg_postinst_${PN}-host-conf () {
#!/bin/sh -e
mv etc/cube-cmd-server.conf-host etc/cube-cmd-server.conf
}

pkg_postinst_${PN}-dom0-conf () {
#!/bin/sh -e
mv etc/cube-cmd-server.conf-dom0 etc/cube-cmd-server.conf
}
