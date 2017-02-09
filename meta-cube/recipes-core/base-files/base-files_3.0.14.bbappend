#
# Normally the /sbin:/usr/sbin:/usr/local/sbin trio is meant for
# just UID == 0 but it will drive old school users nuts if they
# can't run "ifconfig" just to check basic network status.  Give
# them a knob that will influence how /etc/profile handles this.
#
SBIN_FOR_NON_ROOT ?= "0"

# If fstab should be populated with a cgroups entry, set this value
# to "1". LXC and Docker have competing views of cgroups and how the
# hierarchy should be mounted, and hence the decision is sometimes
# better left to the init sequence (versus fstab).
CGROUPS_FSTAB ?= "0"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://profile-add-a-tuning-knob-to-allow-sbin-paths-for-no.patch"

do_install_append () {
	if [ "${SBIN_FOR_NON_ROOT}" = "1" ]; then
		sed -i 's/SBIN_FOR_NON_ROOT=0/SBIN_FOR_NON_ROOT=1/' ${D}${sysconfdir}/profile
	fi
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
