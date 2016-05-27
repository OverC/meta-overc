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
  applied. Supported tools currently include: puppet"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit systemd

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI = " \
    file://source/COPYING \
    file://source/overc-conftools.service \
    file://source/manifests/site.pp \
    file://source/network_prime/files/20-br-int-virt.network \
    file://source/network_prime/files/25-br-int.network \
    file://source/network_prime/files/25-br-int.network.essential \
    file://source/network_prime/files/overc-network-prime.service \
    file://source/network_prime/files/autonetdev \
    file://source/network_prime/manifests/init.pp \
    file://source/network_prime/templates/network_prime.sh.erb \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/${sysconfdir}/puppet/modules/network_prime/manifests
    install -m 644 ${WORKDIR}/source/network_prime/manifests/init.pp \
                   ${D}/${sysconfdir}/puppet/modules/network_prime/manifests/

    install -d ${D}/${sysconfdir}/puppet/modules/network_prime/files
    install -m 755 ${WORKDIR}/source/network_prime/files/autonetdev \
        ${D}/${sysconfdir}/puppet/modules/network_prime/files/
    for file in overc-network-prime.service \
                20-br-int-virt.network 25-br-int.network \
		25-br-int.network.essential; do
        install -m 644 ${WORKDIR}/source/network_prime/files/$file \
	               ${D}/${sysconfdir}/puppet/modules/network_prime/files/
    done

    install -d ${D}/${sysconfdir}/puppet/modules/network_prime/templates
    for template in network_prime.sh.erb; do
         install -m 644 ${WORKDIR}/source/network_prime/templates/$template \
                   ${D}/${sysconfdir}/puppet/modules/network_prime/templates/
    done

    # Puppet manifests
    install -d ${D}/${sysconfdir}/puppet/manifests
    for file in site.pp; do
        install -m 644 ${WORKDIR}/source/manifests/$file ${D}/${sysconfdir}/puppet/manifests/
    done

    # systemd services
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/source/overc-conftools.service ${D}${systemd_unitdir}/system/
}

RDEPENDS_${PN} += " \
    puppet \
    puppet-vswitch \
    puppetlabs-stdlib \
    "

PACKAGES =+ "${PN}-systemd"
RDEPENDS_${PN}-systemd += "${PN}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "overc-conftools.service"

FILES_${PN} +=  " \
    ${base_libdir}/systemd \
"
