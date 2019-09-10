PACKAGES += "${PN}-cube-shared"

DESCRIPTION_${PN}-cube-shared = "Creates a link for /lib/firmware to \
/var/lib/cube/essential/lib/firmware. This is intended to be used in \
conjunction with dom0-ctl-core. This package is only effective if no \
other linux-firmware(-*) packages are installed."

ALLOW_EMPTY_${PN}-cube-shared = "1"

pkg_postinst_${PN}-cube-shared () {
    # Be a nop if any other linux-firmware(-*) pkgs are found
    if [ ! -e $D/lib/firmware ]; then
        mkdir -p $D/var/lib/cube/essential/lib
        ln -sfr $D/var/lib/cube/essential/lib/firmware $D/lib/firmware
        ln -sfr /opt/container/dom0/rootfs/lib/firmware $D/var/lib/cube/essential/lib/firmware
    fi
}