#
# Copyright (C) 2014 Wind River
#

SUMMARY = "util-linux Packages"
DESCRIPTION = "Packages created from util-linux"
PR = "r0"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-util-linux \
    packagegroup-util-linux-block \
    packagegroup-util-linux-console \
    packagegroup-util-linux-uid \
    packagegroup-util-linux-misc \
    packagegroup-util-linux-partition \
    "

RDEPENDS_packagegroup-util-linux = "\
    packagegroup-util-linux-block \
    packagegroup-util-linux-console \
    packagegroup-util-linux-uid \
    packagegroup-util-linux-misc \
    packagegroup-util-linux-partition \
    "

RDEPENDS_packagegroup-util-linux-partition = "\
    util-linux-fdisk \
    util-linux-cfdisk \
    util-linux-sfdisk \
    util-linux-partx \
    "

RDEPENDS_packagegroup-util-linux-console = "\
    util-linux-agetty \
    "

RDEPENDS_packagegroup-util-linux-block = " \
    util-linux-mkfs \
    util-linux-mkfs.cramfs \
    util-linux-fsck.cramfs \
    util-linux-fstrim \
    util-linux-fsck \
    util-linux-blkid \
    util-linux-libblkid \
    util-linux-umount \
    util-linux-mount \
    util-linux-libmount \
    util-linux-swaponoff \
    "

RDEPENDS_packagegroup-util-linux-uid = "\
    util-linux-libuuid \
    util-linux-uuidd \
    util-linux-uuidgen \
    util-linux-findfs \
    "

RDEPENDS_packagegroup-util-linux-misc = "\
    util-linux-losetup \
    util-linux-readprofile \
    util-linux-lscpu \
    util-linux-mcookie \
    util-linux-bash-completion \
    util-linux-hwclock \
    util-linux-getopt \
    util-linux-nsenter \
    "
