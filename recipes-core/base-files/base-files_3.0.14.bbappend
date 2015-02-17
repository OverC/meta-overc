#
# Normally the /sbin:/usr/sbin:/usr/local/sbin trio is meant for
# just UID == 0 but it will drive old school users nuts if they
# can't run "ifconfig" just to check basic network status.  Give
# them a knob that will influence how /etc/profile handles this.
#
SBIN_FOR_NON_ROOT ?= "0"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://profile-add-a-tuning-knob-to-allow-sbin-paths-for-no.patch"

do_install_append () {
	if [ "${SBIN_FOR_NON_ROOT}" = "1" ]; then
		sed -i 's/SBIN_FOR_NON_ROOT=0/SBIN_FOR_NON_ROOT=1/' ${D}${sysconfdir}/profile
	fi
	echo "cgroup               /sys/fs/cgroup       cgroup     defaults              0  0" >> \
		${D}${sysconfdir}/fstab
}


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
           /media /home"

#volatiles = "log tmp"
volatiles = "tmp"
