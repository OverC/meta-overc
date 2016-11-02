SUMMARY = "All packages for container host"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-container \
    packagegroup-container-setup \
    packagegroup-docker \
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
