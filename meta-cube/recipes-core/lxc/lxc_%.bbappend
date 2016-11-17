# cgmanager is needed for running unpriv containers

PACKAGECONFIG_append = " cgmanager templates seccomp"

PACKAGECONFIG[cgmanager] = "--enable-cgmanager=yes,--enable-cgmanager=no,cgmanager,cgmanager"

SYSTEMD_AUTO_ENABLE_${PN}-setup = "enable"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://ovs-up \
    file://ovs-down \
    file://lxc-overlayscan \
    file://lxc-overlayrestore \
    file://overlayrestore \
    file://overlaycreate \
    file://silence_no_escape_lxc-console.patch \
    file://read-write-file-handles-after-EPOLLHUP.patch \
    file://lxc-start-config-Add-lxc.uncontain-to-access-CAP_ADM.patch \
    "

do_install_append(){
	# essential system controls the network, so lxc-net.service is redundant,
	# remove the dependancy from lxc.service to reduce the boottime.

	sed -i 's/lxc-net.service//g'  ${D}${systemd_unitdir}/system/lxc.service
	sed -i 's/\(After=.*$\)/\1 openvswitch-nonetwork.service/' ${D}${systemd_unitdir}/system/lxc.service
	sed -i '1,/ExecStartPre/ {/ExecStartPre/ i\
ExecStartPre=/etc/lxc/lxc-overlayscan\nExecStartPre=/etc/lxc/lxc-overlayrestore
}' ${D}${systemd_unitdir}/system/lxc.service

	# disable the dmesg output on the console when booting the containers,
	# and this will make the system's boot console clean and reduce the boottime.
	if [ -w ${D}${libdir}/lxc/lxc/lxc-containers ]; then
	    sed -i  '2a dmesg -D'  ${D}${libdir}/lxc/lxc/lxc-containers
	else
	    sed -i  '2a dmesg -D'  ${D}${libexecdir}/lxc/lxc-containers
	fi

	# allow containers to connect to an OpenVSwitch bridge (br-int)
	install -d ${D}/etc/lxc/
	install -m 755 ${WORKDIR}/ovs-up ${D}/etc/lxc/ovs-up
	install -m 755 ${WORKDIR}/ovs-down ${D}/etc/lxc/ovs-down

	# add script to scan dir mount with overlay to delete duplicate file
	install -m 755 ${WORKDIR}/lxc-overlayscan ${D}/etc/lxc/lxc-overlayscan
	install -m 755 ${WORKDIR}/lxc-overlayrestore ${D}/etc/lxc/lxc-overlayrestore
	install -m 755 ${WORKDIR}/overlayrestore ${D}/etc/lxc/overlayrestore
	install -m 755 ${WORKDIR}/overlaycreate ${D}/etc/lxc/overlaycreate
}
