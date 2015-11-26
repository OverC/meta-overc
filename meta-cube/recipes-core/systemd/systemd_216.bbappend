PACKAGECONFIG_append = " resolved networkd manpages"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-216:"

SRC_URI += " \
        file://0001-systemd-make-udev-create-or-delete-device-nodes.patch \
       "

