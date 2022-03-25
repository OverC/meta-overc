SUMMARY = "cube-vrf / minimal initialization service"
DESCRIPTION = "A tool for the initial cube-vrf setup"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS:${PN} = "bash"

SRC_URI = " \
    file://vrf-init \
    file://vrf-iface-add \
    file://vrf-iface-del \
"

do_install() {
    install -d ${D}${base_sbindir}
    install -m 0755 ${WORKDIR}/vrf-init ${D}${base_sbindir}/vrf-init

    install -d ${D}/${prefix}
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/vrf-iface-add ${D}/${bindir}/
    install -m 0755 ${WORKDIR}/vrf-iface-del ${D}/${bindir}/
}

FILES:${PN} += " \
    ${sbin} \
    ${bindir} \
"
