SUMMARY = "Additional packages for VM OCI integration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

inherit packagegroup

PACKAGES = "\
    packagegroup-vm-sep \
    "

# runv is currently broken: march 2021 (due to hyperstart)
# packagegroup-runv 

RDEPENDS:${PN} = " \
    libvirt \
    qemu \
    qemu-x86_64 \
"

# hyperstart is currently broken: march 2021
# hyperstart

RDEPENDS:packagegroup-runv = " \
    runv \
"
