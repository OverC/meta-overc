FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI += " \
        file://0001-systemd-make-udev-create-or-delete-device-nodes.patch \
	file://OverC_Allow_RW_sys.patch \
       "

PACKAGECONFIG_append = " iptc"
