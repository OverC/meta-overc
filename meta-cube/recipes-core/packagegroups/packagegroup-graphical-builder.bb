SUMMARY = "All packages for a graphical builder"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r1"

inherit packagegroup

# mandatory
RDEPENDS_${PN} = " \
    packagegroup-xfce \
"

