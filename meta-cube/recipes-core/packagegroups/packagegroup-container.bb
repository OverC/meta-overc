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

RDEPENDS:${PN} = " \
    libvirt \
    libvirt-python \
    packagegroup-lxc \
    packagegroup-docker \
    packagegroup-oci \
    riddler \
"

RDEPENDS:packagegroup-lxc = " \
    lxc \
"
RDEPENDS:packagegroup-lxc-setup = " \
    lxc-setup \
"

RDEPENDS:packagegroup-docker = " \
    docker \
"

RDEPENDS:packagegroup-oci = " \
    virtual/runc \
    oci-systemd-hook \
    oci-runtime-tools \
    oci-image-tools \
"
