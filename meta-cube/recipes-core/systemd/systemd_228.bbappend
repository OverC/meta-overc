FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-228:"

SRC_URI += " \
        file://0001-systemd-make-udev-create-or-delete-device-nodes.patch \
       "

PACKAGECONFIG_append = " iptc"
