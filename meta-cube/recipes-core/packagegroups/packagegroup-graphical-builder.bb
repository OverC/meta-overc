SUMMARY = "All packages for a graphical builder"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

# mandatory
RDEPENDS_${PN} = " \
    packagegroup-xfce \
"

