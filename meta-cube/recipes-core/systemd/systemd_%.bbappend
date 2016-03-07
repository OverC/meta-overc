PACKAGECONFIG_append = " resolved networkd manpages gcrypt"
CONFFILES_${PN} += "${sysconfdir}/systemd/system/getty.target.wants/getty@tty1.service"
