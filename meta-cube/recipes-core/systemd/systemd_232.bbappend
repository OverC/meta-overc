FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI += " \
        file://0001-systemd-make-udev-create-or-delete-device-nodes.patch \
        file://0020-back-port-233-don-t-use-the-unified-hierarchy-for-the-systemd.patch \
       "

PACKAGECONFIG_append = " iptc"
