#
# Original packagegroup Copyright (C) 2010 Intel Corporation
#
# cube essential variant Copyright (C) 2015 Wind River
#

SUMMARY = "Native host packages for OverC"
DESCRIPTION = "Packages required to round out the native host system for OverC"
LICENSE = "MIT"

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
     ${OVERC_COMMON_DISK} \
    "

RDEPENDS_packagegroup-essential-extended = "\
     ${OVERC_COMMON_EXTENDED} \
    "

RDEPENDS_packagegroup-essential-networking = "\
     ${OVERC_COMMON_NETWORKING} \
    "

RDEPENDS_packagegroup-essential-only = "\
     linux-firmware \
     watchdog \
     cube-cmd-server \
     cube-cmd-server-host-conf \
     overc-utils \
    "
