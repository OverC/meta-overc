PACKAGECONFIG_append = " resolved networkd manpages"
CONFFILES_${PN} += "${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service"
