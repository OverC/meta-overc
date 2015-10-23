SUMMARY = "Container Shutdown Notification"
DESCRIPTION = "A service for informing of container shutdown"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS_${PN} = "bash cube-cmd-server-functions"

SRC_URI = "file://container-shutdown-notifier \
           file://container-shutdown-notifier.service \
"
inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "container-shutdown-notifier.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install() {
    install -d ${D}/lib/systemd/system/
    install -m 0644 ${WORKDIR}/container-shutdown-notifier.service ${D}/lib/systemd/system/

    install -d ${D}${base_sbindir}
    install -m 0744 ${WORKDIR}/container-shutdown-notifier ${D}${base_sbindir}
}
