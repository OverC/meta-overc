SUMMARY = "POD Command Server"
DESCRIPTION = "A service for taking action on commands from containers"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://pod-cmd-server \
           file://pod-cmd-server.service \
           file://pod-cmd-server.conf \
           file://pod-cmd-server-functions \
"
RDEPENDS_${PN} = "bash pod-cmd-server-functions"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "pod-cmd-server.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

PACKAGES += "${PN}-dom0-conf ${PN}-host-conf ${PN}-functions"

do_install_append() {
    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/pod-cmd-server.service ${D}/lib/systemd/system/

    install -d ${D}${base_sbindir}
    install -m 0755 ${WORKDIR}/pod-cmd-server ${D}${base_sbindir}

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/pod-cmd-server.conf ${D}${sysconfdir}/pod-cmd-server.conf-host
    install -m 0644 ${WORKDIR}/pod-cmd-server.conf ${D}${sysconfdir}/pod-cmd-server.conf-dom0
    install -m 0644 ${WORKDIR}/pod-cmd-server-functions ${D}${sysconfdir}/pod-cmd-server-functions
}

FILES_${PN} = "${base_sbindir}/pod-cmd-server lib/systemd/system/pod-cmd-server.service"

FILES_${PN}-dom0-conf = "${sysconfdir}/pod-cmd-server.conf-dom0"
FILES_${PN}-host-conf = "${sysconfdir}/pod-cmd-server.conf-host"
FILES_${PN}-functions = "${sysconfdir}/pod-cmd-server-functions"

pkg_postinst_${PN}-host-conf () {
#!/bin/sh -e
mv etc/pod-cmd-server.conf-host etc/pod-cmd-server.conf
# The host will monitor dom0
sed -i 's/%cn%/dom0/' etc/pod-cmd-server.conf
}

pkg_postinst_${PN}-dom0-conf () {
#!/bin/sh -e
mv etc/pod-cmd-server.conf-dom0 etc/pod-cmd-server.conf
# Dom0 will monitor domE
sed -i 's/%cn%/domE/' etc/pod-cmd-server.conf
}
