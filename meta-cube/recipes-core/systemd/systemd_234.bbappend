FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
        file://0001-systemd-make-udev-create-or-delete-device-nodes.patch \
	file://OverC_Allow_RW_sys.patch \
       "

# Temporary patch until runc can be fixed properly to deal with EPOLLHUP
#   see: https://github.com/opencontainers/runc/pull/1455
# After this is resolved and runc is upreved this patch should go away
SRC_URI += "file://Fix-broken-dev-console-when-running-in-docker-contai.patch"

PACKAGECONFIG_append = " iptc"
