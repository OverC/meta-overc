SUMMARY = "All packages for domain 0 container"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-dom0 \
    packagegroup-dom0-extended \
"

# mandatory
RDEPENDS_${PN} = " \
    packagegroup-xfce \
    packagegroup-dom0-extended \
"

RDEPENDS_packagegroup-dom0-extended = "\
    ntp \
    ntpdate \
    ntp-tickadj \
    ntp-utils \
"
