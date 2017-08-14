PACKAGECONFIG_append = " resolved networkd manpages gcrypt"
CONFFILES_${PN} += "${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service"

USERADD_PACKAGES += "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/${BPN}-1 polkitd"

do_install_append() {
	if [ -d ${D}/${datadir}/polkit-1/rules.d ] ; then
		chmod 700 ${D}/${datadir}/polkit-1/rules.d
		chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
	fi
}

