inherit hosts

# Set the root password for all images to be root/root
inherit extrausers
EXTRA_USERS_PARAMS ?= "usermod -p '\$6\$itWJK/a95NGi5AVs\$0zlkWdhpXg5CWtEC0YxIH8P.BwaKTOmaSiUPOC8YdqQPZz66UiRt2oZa5UWpXXq8AfdiSSCpMz6b.zYNxCK1o/' root;"

ROOTFS_POSTPROCESS_COMMAND += "builder_configure_host ; "
ROOTFS_POSTPROCESS_COMMAND += "systemd_autostart_fixups ; "

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


ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit_network; ', 'systemd_bridged_network; ', d)}"

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
