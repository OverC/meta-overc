#
# Original packagegroup Copyright (C) 2010 Intel Corporation
#
# cube essential variant Copyright (C) 2015 Wind River
#

SUMMARY = "Native host packages for OverC"
DESCRIPTION = "Packages required to round out the native host system for OverC"
LICENSE = "MIT"

# grr - packagegroup is special and wants to be allarch, which
# doesn't work so well if we want to filter grub for MACHINE_FEATURES, etc.
PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

require overc-common-pkgdefs.inc

PACKAGES = "\
     packagegroup-essential \
     packagegroup-essential-disk \
     packagegroup-essential-extended \
     packagegroup-essential-networking \
     packagegroup-essential-only \
    "

RDEPENDS_packagegroup-essential = "\
     packagegroup-essential-disk \
     packagegroup-essential-extended \
     packagegroup-essential-networking \
     packagegroup-essential-only \
    "

RDEPENDS_packagegroup-essential-disk = " \
     btrfs-tools \
     e2fsprogs-tune2fs \
     dosfstools \
     mdadm \
    "

RDEPENDS_packagegroup-essential-extended = "\
     cpio \
     dmidecode \
     dtach \
     ed \
     fbset \
     findutils \
     grep \
     gzip \
     kernel-modules \
     less \
     ncurses-terminfo \
     pciutils \
     screen \
     tar \
     which \
     zip \
     gawk \
     bzip2 \
     procps \
     util-linux \
     kmod \
     sed \
     coreutils \
     e2fsprogs \
     ${OVERC_COMMON_EXTENDED_ARCH} \
    "

RDEPENDS_packagegroup-essential-networking = "\
     ifupdown \
     iproute2 \
     iputils \
     iptables \
     iptables-modules \
     net-tools \
     netcat \
     dhcp-client \
    "

RDEPENDS_packagegroup-essential-only = "\
     linux-firmware-cube-shared \
     watchdog \
    "
