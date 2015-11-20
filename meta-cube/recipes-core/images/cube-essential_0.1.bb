SUMMARY = "A minimal native host image for OverC"
DESCRIPTION = "Small native host image for OverC capable of launching \
               the domain0 container."
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"


# we don't want this to be a container image even
# configure with --enable-container=yes
USE_DEPMOD = "1"
CUBE_ESSENTIAL_EXTRA_INSTALL ?= "kernel-modules"

PACKAGE_EXCLUDE = "busybox*"

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-essential \
                  packagegroup-util-linux \
                  packagegroup-container \
		  packagegroup-container-setup \
		  cube-update \
                  ${CUBE_ESSENTIAL_EXTRA_INSTALL} \
                 "

IMAGE_FEATURES += "package-management"

IMAGE_FSTYPES = "tar.bz2"

INITRD = "True"
INITRAMFS_IMAGE = "cube-builder-initramfs"
# We want it separate, and not bundled with the kernel.
INITRAMFS_IMAGE_BUNDLE = ""

inherit core-image
inherit builder-base
