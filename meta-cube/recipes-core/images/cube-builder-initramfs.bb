# Simple initramfs image. Mostly used for live images.
DESCRIPTION = "Small image capable of booting a device. The kernel includes \
the Minimal RAM-based Initial Root Filesystem (initramfs), which finds the \
first 'init' program more efficiently."

CUBE_BUILDER_INITRAMFS_EXTRA_INSTALL ?= ""

# Archived variant for running a full installer from the initramfs
# PACKAGE_INSTALL = "initramfs-cube-builder bash kmod bzip2 vim which sed tar kbd coreutils util-linux grep gawk udev mdadm base-passwd ${ROOTFS_BOOTSTRAP_INSTALL}"
PACKAGE_INSTALL = "initramfs-cube-builder bash kmod bzip2 sed tar kbd coreutils util-linux grep gawk udev mdadm base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} ${CUBE_BUILDER_INITRAMFS_EXTRA_INSTALL}"
PACKAGE_EXCLUDE = "${@bb.utils.contains('DISTRO', 'overc', 'ybox busybox-dev busybox-udhcpc busybox-dbg busybox-ptest busybox-udhcpd busybox-hwclock busybox-syslog', '', d )}"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

export IMAGE_BASENAME = "cube-builder-initramfs"
IMAGE_LINGUAS = ""

LICENSE = "MIT"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit core-image

IMAGE_ROOTFS_SIZE = "8192"

BAD_RECOMMENDATIONS += "busybox-syslog"
