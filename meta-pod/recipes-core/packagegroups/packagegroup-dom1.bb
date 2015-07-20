#
# Original packagegroup Copyright (C) 2010 Intel Corporation
#
# domain 1 variant Copyright (C) 2015 Wind River
#

SUMMARY = "Domain 1 facilities for OverC"
DESCRIPTION = "Packages required to define domain 1 pod for OverC"
LICENSE = "MIT"


inherit packagegroup

require overc-common-pkgdefs.inc

PACKAGES = "\
     packagegroup-dom1 \
     packagegroup-dom1-fs \
     packagegroup-dom1-extended \
     packagegroup-dom1-networking \
     packagegroup-dom1-tools \
    "

RDEPENDS_packagegroup-dom1 = "\
     packagegroup-dom1-fs \
     packagegroup-dom1-extended \
     packagegroup-dom1-networking \
     packagegroup-dom1-tools \
    "

RDEPENDS_packagegroup-dom1-fs = " \
     ${OVERC_COMMON_DISK} \
     ${OVERC_COMMON_FS} \
     dosfstools \
     fuse \
     fuse-utils \
     nfs-utils \
     nfs-utils-client \
    "

RDEPENDS_packagegroup-dom1-extended = "\
     ${OVERC_COMMON_EXTENDED} \
     ${OVERC_EXTRA_EXTENDED} \
     acpid \
     at \
     cracklib \
     git \
     git-bash-completion \
     gnupg \
     logrotate \
     mailx \
     mime-support \
     msmtp \
     mutt \
     ntp \
     openssh-sftp-server \
     rpcbind \
     sysklogd \
     upower \
    "

RDEPENDS_packagegroup-dom1-networking = "\
     ${OVERC_COMMON_NETWORKING} \
     ${OVERC_EXTRA_NETWORKING} \
     bind-utils \
     bluez5 \
     resolvconf \
    "

RDEPENDS_packagegroup-dom1-tools = "\
     ${OVERC_COMMON_TOOLS} \
     curl \
     eject \
     git-perltools \
     minicom \
     ntp-utils \
     smartmontools \
     usbutils \
    "
