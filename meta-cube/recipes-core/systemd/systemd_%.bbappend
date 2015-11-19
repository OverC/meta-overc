PACKAGECONFIG_append = " resolved networkd manpages"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
        file://0001-systemd-make-some-services-container-awared.patch \
"

