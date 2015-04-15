inherit hosts

ROOTFS_POSTPROCESS_COMMAND += "builder_configure_host ; "

builder_configure_host() {
#    bbnote "builder: configuring host"

    echo "${TARGETNAME}" > ${IMAGE_ROOTFS}/etc/hostname

}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit_network; ', 'systemd_network; ', d)}"

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

sysvinit_network () {
        install -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/network
        cat << EOF > ${IMAGE_ROOTFS}${sysconfdir}/systemd/network/wired.network
# loopback
auto lo
iface lo inet loopback

# The primary network interface
auto eth0
iface eth0 inet dhcp
EOF
}
