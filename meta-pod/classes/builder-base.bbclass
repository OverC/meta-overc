inherit hosts

ROOTFS_POSTPROCESS_COMMAND += "builder_configure_host ; "

builder_configure_host() {
#    bbnote "builder: configuring host"

    echo "${TARGETNAME}" > ${IMAGE_ROOTFS}/etc/hostname

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
