# If fstab should be populated with a cgroups entry, set this value
# to "1". LXC and Docker have competing views of cgroups and how the
# hierarchy should be mounted, and hence the decision is sometimes
# better left to the init sequence (versus fstab).
CGROUPS_FSTAB ?= "0"

do_install_append () {
	if [ "${CGROUPS_FSTAB}" = "1" ]; then
		echo "cgroup		   /sys/fs/cgroup	cgroup	   defaults		 0  0" >> ${D}${sysconfdir}/fstab
	fi
}


#
# move from var/volatile/log removed and var/log added.
#
dirs755_remove = "${localstatedir}/volatile/log"
dirs755_append = " ${localstatedir}/log"

#volatiles = "log tmp"
volatiles = "tmp"
