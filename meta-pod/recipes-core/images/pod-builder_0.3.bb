SUMMARY = "An image creating a file system for a build server itself"
DESCRIPTION = "An image capable of building the system that you can deploy on a server."
HOMEPAGE = "http://www.windriver.com"

# fixme ; point at layer copy someday.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
		    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

POD_BUILDER_EXTRA_INSTALL ?= ""

PACKAGE_EXCLUDE = "busybox busybox-dev busybox-udhcpc busybox-dbg busybox-ptest busybox-udhcpd busybox-hwclock busybox-syslog"

# fixme: core-boot has a hard dep on boogerbox
IMAGE_INSTALL += "packagegroup-core-boot \
		  packagegroup-core-ssh-openssh \
		  packagegroup-core-full-cmdline \
		  packagegroup-util-linux \
		  packagegroup-builder \
		  packagegroup-container \
		  ${POD_BUILDER_EXTRA_INSTALL} \
		 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES = "tar.bz2"

INITRD = "True"
INITRAMFS_IMAGE = "pod-builder-initramfs"
# We want it separate, and not bundled with the kernel.
INITRAMFS_IMAGE_BUNDLE = ""

inherit core-image
inherit builder-base
