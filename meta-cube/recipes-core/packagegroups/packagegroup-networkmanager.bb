SUMMARY = "All packages for Network Manager"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-networkmanager \
    "

RDEPENDS_${PN} = "\
    networkmanager \
    network-manager-applet \
    gnome-keyring \
    udisks \
    "
