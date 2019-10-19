inherit hosts

# Set the root password for all images to be root/root
inherit extrausers
EXTRA_USERS_PARAMS ?= "usermod -p '\$6\$itWJK/a95NGi5AVs\$0zlkWdhpXg5CWtEC0YxIH8P.BwaKTOmaSiUPOC8YdqQPZz66UiRt2oZa5UWpXXq8AfdiSSCpMz6b.zYNxCK1o/' root;"

ROOTFS_POSTPROCESS_COMMAND += "builder_configure_host ; "
ROOTFS_POSTPROCESS_COMMAND += "systemd_autostart_fixups ; "
ROOTFS_POSTPROCESS_COMMAND += "tests_cleanup ; "

# nuke any python tests that may have snuck through the build, these just
# waste space
tests_cleanup() {
    set +e
    # anything blacklisted already knows how to clean up after itself
    CLEANUP_BLACKLIST="ansible"
    rm_list=""
    for d in $(find "${IMAGE_ROOTFS}" -name 'test' -type d); do
	echo $d | grep -q python
	if [ $? -eq 0 ]; then
	    rm_list="$rm_list $d"
	fi
    done
    for d in $(find "${IMAGE_ROOTFS}" -name 'tests' -type d); do
	echo $d | grep -q python
	if [ $? -eq 0 ]; then
	    rm_list="$rm_list $d"
	fi
    done
    for d in $rm_list; do
	echo ${CLEANUP_BLACKLIST} | grep -q $d
	do_rm=t
	for b in ${CLEANUP_BLACKLIST}; do
	    echo "${d}" | grep -q "${b}"
	    if [ $? -eq 0 ]; then
		do_rm=
	    fi
	done
	if [ -n "${do_rm}" ]; then
	    rm -rf ${d}
	fi
    done
}

builder_configure_host() {
#    bbnote "builder: configuring host"

    echo "${TARGETNAME}" > ${IMAGE_ROOTFS}/etc/hostname

}

systemd_autostart_fixups() {
    if [ -d "${IMAGE_ROOTFS}/etc/rpm-postinsts" ]; then
        for post in ${IMAGE_ROOTFS}/etc/rpm-postinsts/*; do
            sed -i 's/systemctl restart/systemctl --no-block restart/' $post
        done
    fi
}

def select_networking_config(d):
    if bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        if bb.utils.contains('DISTRO_FEATURES', 'overc_bridge_networking', True, False, d):
            return "sysvinit_network; "
        else:
            return "sysvinit_network_unsupported; "
    else:
        if bb.utils.contains('DISTRO_FEATURES', 'overc_bridge_networking', True, False, d):
            return "systemd_bridged_network; "
        else:
            return "systemd_openvswitch_network; "

ROOTFS_POSTPROCESS_COMMAND += "${@select_networking_config(d)}"

systemd_network () {
        install -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/network
        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/wired.network
[Match]
Name=en*

[Network]
DHCP=yes
EOF

        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/wired-network-ifnames.network
[Match]
Name=eth*

[Network]
DHCP=yes
EOF
}

systemd_bridged_network () {

        install -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/network

        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/bridge.netdev
[NetDev]
Name=br0
Kind=bridge
EOF

        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/bridge.network
[Match]
Name=br0

[Network]
DHCP=v4
EOF

        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/wired.network
[Match]
Name=en*

[Network]
Bridge=br0
EOF

        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/wired-network-ifnames.network
[Match]
Name=eth*

[Network]
Bridge=br0
EOF
}

sysvinit_network () {
        install -d ${IMAGE_ROOTFS}${sysconfdir}/etc/network
}

sysvinit_network_unsupported () {
        echo "ERROR: Unsupported configuration. No support for OpenVSwitch networking with sysvinit"
	exit 1
}

systemd_openvswitch_network () {
        install -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/network
        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/20-wired.network
[Match]
Name=en* eth*

[Network]
DHCP=ipv4
EOF
}
