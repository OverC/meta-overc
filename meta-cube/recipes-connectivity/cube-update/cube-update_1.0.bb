#
# Copyright (C) 2015 Wind River Systems, Inc.
#

SUMMARY = "Service to update packages automatically"
DESCRIPTION = "Service to update RPM packages periodically, \
based on the overc host update command, and integrated with \
the systemd and sysvinit init systems."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
RDEPENDS_${PN} = "python-smartpm"

SRC_URI += " \
	file://cubeupdated \
	file://cubepkgcheck \
	file://cube-update.service \
	file://config.default \
	"

inherit autotools update-rc.d systemd

FILES_${PN} += " \
	${sysconfdir}/cube-update/* \
	${sysconfdir}/init.d/* \
	${systemd_unitdir}/system/* \
	"

SYSTEMD_SERVICE_${PN} = "cube-update.service"
CONFFILES_${PN} = "${sysconfdir}/cube-update/config.default"
SYSTEMD_AUTO_ENABLE = "${@base_contains('PULSAR_UNATTENDED_UPGRADE','true','enable','disable', d)}"

INITSCRIPT_NAME = "cubeupdated"
INITSCRIPT_PARAMS = "start 49 2 3 4 5 . stop 51 0 1 6 ."

do_compile[noexec] = "1"

do_install () {
	# cube-update service:
	install -d -m 0755 ${D}${sysconfdir}/cube-update
	install -m 0555 ${WORKDIR}/config.default ${D}${sysconfdir}/cube-update/config.default
	install -m 0555 ${WORKDIR}/cubeupdated ${D}${sysconfdir}/cube-update/cubeupdated
	install -m 0555 ${WORKDIR}/cubepkgcheck ${D}${sysconfdir}/cube-update/cubepkgcheck

	if ${@base_contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
	    # systemd:
	    install -d -m 0755 ${D}${systemd_unitdir}/system
	    install -d -m 0755 ${D}${systemd_unitdir}/system/multi-user.target.wants
	    install -m 0644 ${WORKDIR}/cube-update.service ${D}${systemd_unitdir}/system/cube-update.service
	else
	    # sysvinit:
	    install -d -m 0755 ${D}${sysconfdir}/init.d
	    install -m 0555 ${WORKDIR}/cubeupdated ${D}${sysconfdir}/init.d/cubeupdated
	fi
}
