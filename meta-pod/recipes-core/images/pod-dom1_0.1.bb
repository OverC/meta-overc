SUMMARY = "A container image for OverC domain 1 which is used for system services"
DESCRIPTION = "This is an OverC container image used for providing system services"
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_EXCLUDE = "busybox* "

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-util-linux \
                  packagegroup-core-ssh-openssh \
                  packagegroup-container \
                  packagegroup-dom1 \
                 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES = "tar.bz2"

inherit core-image
inherit builder-base
