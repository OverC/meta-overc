# cgmanager is needed for running unpriv containers

PACKAGECONFIG_append = " cgmanager"

PACKAGECONFIG[cgmanager] = "--enable-cgmanager=yes,--enable-cgmanager=no,cgmanager,cgmanager"

SYSTEMD_AUTO_ENABLE_${PN}-setup = "enable"

do_install_append(){
	# essential system controls the network, so lxc-net.service is redundant,
	# remove the dependancy from lxc.service to reduce the boottime.

	sed -i 's/lxc-net.service//g'  ${D}${systemd_unitdir}/system/lxc.service

	# disable the dmesg output on the console when booting the containers,
	# and this will make the system's boot console clean and reduce the boottime.
	sed -i  '2a dmesg -D'  ${D}/${libdir}/lxc/lxc/lxc-containers
}
