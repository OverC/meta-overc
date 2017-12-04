PACKAGECONFIG_append = " resolved networkd manpages gcrypt"
CONFFILES_${PN} += "${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service"

do_install_append () {
	# Check kernel-release module path before loading modules, to prevent systemd-modules-load
	# fail due to path missing on containers, which can happen after essential image upgraded.
	sed -i "0,/ConditionDirectoryNotEmpty=/s#\(ConditionDirectoryNotEmpty=.*\)#ConditionDirectoryNotEmpty=/lib/modules/%v\n\1#" ${D}${systemd_system_unitdir}/systemd-modules-load.service
}
