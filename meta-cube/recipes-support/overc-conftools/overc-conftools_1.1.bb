#
# Core OverC configurations should be included in this package.
# Alternatively packages can RDEPEND on this package and make
# use of the included services to ensure their configurations
# are applied at the appropriate time.
#
SUMMARY = "OverC configuration tools"
DESCRIPTION = "Set of configuration files and systemd services \
  that images can include to allow for customization of images \
  through a set of supported tools. Including this package in an \
  image does not necessarily result in the configurations being \
  applied."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit systemd

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI = " \
    file://source/COPYING \
    file://source/overc-conftools.service \
    file://source/overc-conf.sh \
    file://source/overc-conf.d/systemid-set.sh \
    file://source/cube-admin \
    file://source/cube-network \
    file://source/cube-netconfig \
    file://source/c3-ipcfg \
    file://source/oci-device \
    file://source/dhclient-script.container \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/${sysconfdir}/overc-conf
    install -m 755 ${WORKDIR}/source/overc-conf.sh ${D}/${sysconfdir}/overc-conf/
    install -d ${D}/${sysconfdir}/overc-conf/overc-conf.d
    install -m 755 ${WORKDIR}/source/overc-conf.d/systemid-set.sh ${D}/${sysconfdir}/overc-conf/overc-conf.d/

    #to create an empty system-id file to tell overc-installer
    #to bind mount it to dom0/cube-desktop to share one system
    #id between them.
    touch ${D}/${sysconfdir}/system-id

    # systemd services
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/source/overc-conftools.service ${D}${systemd_unitdir}/system/

    install -d ${D}/${libexecdir}/oci/hooks.d/
    install -d ${D}/${libexecdir}/cube/hooks.d/
    install -m 755 ${WORKDIR}/source/cube-admin ${D}/${libexecdir}/cube/hooks.d/
    install -m 755 ${WORKDIR}/source/cube-network ${D}/${libexecdir}/cube/hooks.d/
    install -m 755 ${WORKDIR}/source/cube-netconfig ${D}/${libexecdir}/cube/hooks.d/
    install -m 755 ${WORKDIR}/source/oci-device ${D}/${libexecdir}/oci/hooks.d/

    install -d ${D}${sbindir}
    install -m755 ${WORKDIR}/source/dhclient-script.container ${D}${sbindir}
    install -m755 ${WORKDIR}/source/c3-ipcfg ${D}${sbindir}
}

RDEPENDS_${PN} += " \
    bash \
    "

PACKAGES =+ "${PN}-systemd"
RDEPENDS_${PN}-systemd += "${PN}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "overc-conftools.service"

FILES_${PN} +=  " \
    ${base_libdir}/systemd \
    ${sbindir} \
"
FILES_${PN} += "${libexecdir}/oci/hooks.d/"
FILES_${PN} += "${libexecdir}/cube/hooks.d/"
