SUMMARY = "All packages for container host"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-container \
    packagegroup-container-setup \
    packagegroup-docker \
    packagegroup-oci \
    "

RDEPENDS_${PN} = " \
    libvirt \
    libvirt-python \
    lxc \
    qemu \
"

RDEPENDS_${PN}-setup = " \
    lxc-setup \
"

RDEPENDS_packagegroup-docker = " \
    docker \
    libvirt \
    libvirt-python \
"
RDEPENDS_packagegroup-oci = " \
    virtual/runc \
    oci-systemd-hook \
    oci-register-machine \
    oci-runtime-tools \
    oci-image-tools \
    netns \
    riddler \
"
