SUMMARY = "dom0 / minimal initialization service"
DESCRIPTION = "A tool for the initial dom0 setup"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS:${PN} = "overc-utils util-linux bash"

SRC_URI = "file://dom0-containers \
           file://dom0-ctl-core.service \
           file://dom0-ctl-core.conf \
           file://firmware-sync \
"

SRC_FILES_LIST="dom0-containers \
"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "dom0-ctl-core.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}/${sbindir}
    for i in ${SRC_FILES_LIST}; do
        install -m 0755 ${WORKDIR}/${i} ${D}/${sbindir}
    done

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/dom0-ctl-core.service ${D}${systemd_unitdir}/system/

    install -d ${D}/${sysconfdir}
    install -m 0744 ${WORKDIR}/dom0-ctl-core.conf ${D}/${sysconfdir}

    install -d ${D}/${sysconfdir}/dom0.d
    install -m 0770 ${WORKDIR}/firmware-sync ${D}/${sysconfdir}/dom0.d/
}

FILES:${PN} += "${sbin} \
                ${systemd_unitdir}/system \
                ${sysconfdir} \
"
