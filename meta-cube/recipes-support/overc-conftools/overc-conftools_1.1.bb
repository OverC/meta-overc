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
    file://source/network_prime/files/20-br-int-virt.network \
    file://source/network_prime/files/25-br-int.network \
    file://source/network_prime/files/25-br-int.network.essential \
    file://source/network_prime/files/overc-network-prime.service \
    file://source/network_prime/files/overc-network-prime-port-forward.service \
    file://source/network_prime/files/autonetdev \
    file://source/network_prime/files/network_prime.sh.erb \
    file://source/network_prime/files/network_prime_port_forward.sh.erb \
    file://source/system/systemid-set.sh \
"

S = "${WORKDIR}"

do_install() {

    install -d ${D}/${sysconfdir}/overc-conf/network_prime
    install -m 755 ${WORKDIR}/source/network_prime/files/autonetdev \
        ${D}/${sysconfdir}/overc-conf/network_prime/

    for file in overc-network-prime.service \
                overc-network-prime-port-forward.service \
                20-br-int-virt.network 25-br-int.network \
		25-br-int.network.essential \
                network_prime.sh.erb \
                network_prime_port_forward.sh.erb; do
        install -m 644 ${WORKDIR}/source/network_prime/files/$file \
	               ${D}/${sysconfdir}/overc-conf/network_prime/
    done

    install -d ${D}/${sysconfdir}/overc-conf/system
    install -m 755 ${WORKDIR}/source/system/systemid-set.sh ${D}/${sysconfdir}/overc-conf/system/

    #to create an empty system-id file to tell overc-installer
    #to bind mount it to dom0/cube-desktop to share one system
    #id between them.
    touch ${D}/${sysconfdir}/system-id

    # systemd services
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/source/overc-conftools.service ${D}${systemd_unitdir}/system/
}

PACKAGES =+ "${PN}-systemd"
RDEPENDS_${PN}-systemd += "${PN}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "overc-conftools.service"

FILES_${PN} +=  " \
    ${base_libdir}/systemd \
"
