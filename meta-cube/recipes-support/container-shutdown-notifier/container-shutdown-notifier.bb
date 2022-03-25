SUMMARY = "Container Shutdown Notification"
DESCRIPTION = "A service for informing of container shutdown"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS:${PN} = "bash overc-utils"

SRC_URI = "file://container-shutdown-notifier \
           file://container-shutdown-notifier.service \
"
inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "container-shutdown-notifier.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/container-shutdown-notifier.service ${D}/${systemd_system_unitdir}

    install -d ${D}${base_sbindir}
    install -m 0744 ${WORKDIR}/container-shutdown-notifier ${D}${base_sbindir}
}
