SUMMARY = "Domain 0 Container Control Tool"
DESCRIPTION = "A tool for deploying, querying, etc lxc containers from Domain 0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS_${PN} = "util-linux lxc"

SRC_URI = "file://dom0_contctl \
           file://lxc_driver.sh \
"

do_install() {
    dom0_contctl_dir=${D}/opt/dom0-contctl

    install -d ${D}/opt/
    install -d ${dom0_contctl_dir}
    install -m 0755 ${WORKDIR}/dom0_contctl ${dom0_contctl_dir}
    install -m 0644 ${WORKDIR}/lxc_driver.sh ${dom0_contctl_dir}
}

FILES_${PN} += "/opt"
