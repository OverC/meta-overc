SUMMARY = "All packages for Network Manager"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-networkmanager \
    "

RDEPENDS_${PN} = "\
    networkmanager \
    network-manager-applet \
    gnome-keyring \
    udisks2 \
    "
