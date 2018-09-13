FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://grub-enable-serial-console-by-default.patch"

BBCLASSEXTEND = "native"
