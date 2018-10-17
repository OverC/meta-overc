#
# Original packagegroup Copyright (C) 2010 Intel Corporation
#
# dom0 variant Copyright (C) 2015 Wind River
#

SUMMARY = "Domain 0 facilities for OverC"
DESCRIPTION = "Packages required to define domain 0 cube for OverC"
LICENSE = "MIT"

# grr - packagegroup is special and wants to be allarch, which
# doesn't work so well if we want to filter grub for MACHINE_FEATURES, etc.
PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

require overc-common-pkgdefs.inc

PACKAGES = "\
     packagegroup-dom0 \
     packagegroup-dom0-doc \
     packagegroup-dom0-fs \
     packagegroup-dom0-extended \
     packagegroup-dom0-networking \
     packagegroup-dom0-python \
     packagegroup-dom0-tools \
     packagegroup-dom0-only \
    "

RDEPENDS_packagegroup-dom0-debug = "\
     packagegroup-dom0-doc \
     packagegroup-dom0-fs \
     packagegroup-dom0-extended \
     packagegroup-dom0-networking \
     packagegroup-dom0-perl \
     packagegroup-dom0-python \
     packagegroup-dom0-tools \
     packagegroup-dom0-only \
    "

RDEPENDS_packagegroup-dom0 = "\
     packagegroup-dom0-fs \
     packagegroup-dom0-extended \
     packagegroup-dom0-networking \
     packagegroup-dom0-tools \
     packagegroup-dom0-only \
    "

RDEPENDS_packagegroup-dom0-fs = " \
     ${OVERC_DOM0_DISK} \
     ${OVERC_DOM0_FS} \
    "

RDEPENDS_packagegroup-dom0-doc = " \
     ${OVERC_COMMON_DOC} \
    "

RDEPENDS_packagegroup-dom0-perl = " \
     ${OVERC_COMMON_PERL} \
    "

RDEPENDS_packagegroup-dom0-python = " \
     ${OVERC_COMMON_PYTHON} \
    "

RDEPENDS_packagegroup-dom0-extended = "\
     ${OVERC_DOM0_EXTENDED} \
    "

RDEPENDS_packagegroup-dom0-networking = "\
     ${OVERC_DOM0_NETWORKING} \
     ${OVERC_DOM0_EXTRA_NETWORKING} \
    "

RDEPENDS_packagegroup-dom0-tools = "\
     killall \
     lsof \
     setserial \
     socat \
     nanomsg \
     sudo \
     time \
     overc-utils \
     nanoio \
     ca-certificates \
     udocker \
     container-shutdown-notifier \
    "

RDEPENDS_packagegroup-dom0-only = "\
     linux-firmware \
    "
