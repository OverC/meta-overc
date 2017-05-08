SUMMARY = "Domain 0 Container Control Tool"
DESCRIPTION = "A tool for deploying, querying, etc lxc containers from Domain 0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

RDEPENDS_${PN} = "util-linux lxc bash python cgmanager"

S = "${WORKDIR}"

SRC_URI = "file://dom0_contctl \
           file://lxc_driver.sh \
           file://dom0-containers \
           file://dom0-contctl.service \
           file://lxc_common_helpers.sh \
           file://lxc_driver_net.sh \
           file://lxc_hook_net_pre-mount.sh \
           file://dpdk_nic_bind_wrapper.py \
           file://lxc_hook_net_mount.sh \
           file://dom0-contctl.conf \
           file://lxc_launch_group.sh \
"

SRC_FILES_LIST="dom0_contctl \
lxc_driver.sh \
dom0-containers \
lxc_common_helpers.sh \
lxc_driver_net.sh \
lxc_hook_net_pre-mount.sh \
dpdk_nic_bind_wrapper.py \
lxc_hook_net_mount.sh \
lxc_launch_group.sh \
"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "dom0-contctl.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

systemd_postinst() {
OPTS=""

if [ -n "$D" ]; then
    OPTS="--root=$D"
fi

if type systemctl >/dev/null 2>/dev/null; then
	systemctl $OPTS ${SYSTEMD_AUTO_ENABLE} ${SYSTEMD_SERVICE}
fi
}


do_install() {
    dom0_contctl_dir=${D}/opt/dom0-contctl

    install -d ${D}/opt/
    install -d ${dom0_contctl_dir}
    for i in ${SRC_FILES_LIST}; do
        install -m 0755 ${WORKDIR}/${i} ${dom0_contctl_dir}
    done

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/dom0-contctl.service ${D}${systemd_system_unitdir}

    install -d ${D}/${sysconfdir}
    install -m 0744 ${WORKDIR}/dom0-contctl.conf ${D}/${sysconfdir}/

    if ${@bb.utils.contains("MACHINE", "xilinx-zynq", "true", "false", d)} ; then
        if [ -e ${D}${systemd_system_unitdir}/dom0-contctl.service ]; then
            sed -i 's/^StandardOutput=.*$/StandardOutput=tty/' ${D}${systemd_system_unitdir}/dom0-contctl.service
            sed -i 's/^StandardError=.*$/StandardError=tty/' ${D}${systemd_system_unitdir}/dom0-contctl.service
            sed -i '/^StandardError=/a TTYPath=/dev/ttyPS0' ${D}${systemd_system_unitdir}/dom0-contctl.service
        fi
    fi
}

FILES_${PN} += "/opt \
		${systemd_system_unitdir} \
                ${sysconfdir} \
"
