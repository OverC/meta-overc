#
# Below is just the original with var/volatile/log removed and var/log added.
#
dirs755 = "/bin /boot /dev ${sysconfdir} ${sysconfdir}/default \
           ${sysconfdir}/skel /lib /mnt /proc ${ROOT_HOME} /run /sbin \
           ${prefix} ${bindir} ${docdir} /usr/games ${includedir} \
           ${libdir} ${sbindir} ${datadir} \
           ${datadir}/common-licenses ${datadir}/dict ${infodir} \
           ${mandir} ${datadir}/misc ${localstatedir} \
           ${localstatedir}/backups ${localstatedir}/lib \
           /sys ${localstatedir}/lib/misc ${localstatedir}/spool \
           ${localstatedir}/volatile ${localstatedir}/log \
           /media"

#volatiles = "log tmp"
volatiles = "tmp"
