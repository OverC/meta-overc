SUMMARY = "A minimal native host image for OverC"
DESCRIPTION = "Small native host image for OverC capable of launching \
               the domain0 container."
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

POD_ESSENTIAL_EXTRA_INSTALL ?= ""

PACKAGE_EXCLUDE = "busybox*"

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-essential \
                  packagegroup-util-linux \
                  packagegroup-container \
		  packagegroup-container-setup \
                  ${POD_ESSENTIAL_EXTRA_INSTALL} \
                 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES = "tar.bz2"

INITRD = "True"
INITRAMFS_IMAGE = "pod-builder-initramfs"
# We want it separate, and not bundled with the kernel.
INITRAMFS_IMAGE_BUNDLE = ""

inherit core-image
inherit builder-base
