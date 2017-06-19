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
  applied. Supported tools currently include: ansible"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit systemd

FILESEXTRAPATHS_prepend := "${THISDIR}:"

CONFFILES_${PN} += "${sysconfdir}/system-id"

SRC_URI = " \
    file://source/COPYING \
    file://source/overc-conftools.service \
    file://source/network_prime/files/25-veth0.network \
    file://source/network_prime/files/25-br-int.network.essential \
    file://source/network_prime/files/overc-network-prime-port-forward.service \
    file://source/network_prime/files/network_prime_port_forward.sh.erb \
    file://source/system/systemid-set.sh \
    file://source/ansible/essential.yml \
    file://source/ansible/netprime.yml \
    file://source/ansible/overc_config_vars.yml \
    file://source/ansible/overc.yml \
    file://source/ansible/post.yml \
    file://source/ansible/setup_offset.yml \
    file://source/essential_rw.sh \
"

S = "${WORKDIR}"

do_install() {

    install -d ${D}/${sysconfdir}/overc-conf/network_prime

    for file in overc-network-prime-port-forward.service \
                25-veth0.network \
		25-br-int.network.essential \
                network_prime_port_forward.sh.erb; do
        install -m 644 ${WORKDIR}/source/network_prime/files/$file \
	               ${D}/${sysconfdir}/overc-conf/network_prime/
    done

    install -d ${D}/${sysconfdir}/overc-conf/system
    install -m 755 ${WORKDIR}/source/system/systemid-set.sh ${D}/${sysconfdir}/overc-conf/system/

    install -d ${D}/${sysconfdir}/overc-conf/ansible
    install -m 644 ${WORKDIR}/source/ansible/* ${D}/${sysconfdir}/overc-conf/ansible/

    #to create an empty system-id file to tell overc-installer
    #to bind mount it to dom0/cube-desktop to share one system
    #id between them.
    touch ${D}/${sysconfdir}/system-id

    # systemd services
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/source/overc-conftools.service ${D}${systemd_unitdir}/system/

    install -d ${D}/${sysconfdir}/profile.d/
    install -m 0644 ${WORKDIR}/source/essential_rw.sh ${D}/${sysconfdir}/profile.d/
}

RDEPENDS_${PN} += " \
    python-ansible \
    bash \
    "

PACKAGES =+ "${PN}-systemd"
RDEPENDS_${PN}-systemd += "${PN}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "overc-conftools.service"

FILES_${PN} +=  " \
    ${base_libdir}/systemd \
"
