SUMMARY = "All packages for container host"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-container \
    packagegroup-lxc \
    packagegroup-container-setup \
    packagegroup-docker \
    packagegroup-oci \
    "

RDEPENDS_${PN} = " \
    libvirt \
    libvirt-python \
    packagegroup-lxc \
    packagegroup-docker \
    packagegroup-oci \
    riddler \
"

RDEPENDS_packagegroup-lxc = " \
    lxc \
"
RDEPENDS_packagegroup-lxc-setup = " \
    lxc-setup \
"

RDEPENDS_packagegroup-docker = " \
    docker \
"

RDEPENDS_packagegroup-oci = " \
    virtual/runc \
    oci-systemd-hook \
    oci-runtime-tools \
    oci-image-tools \
"
