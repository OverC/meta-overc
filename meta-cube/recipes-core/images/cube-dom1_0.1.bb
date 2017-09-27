SUMMARY = "A container image for OverC domain 1 which is used for system services"
DESCRIPTION = "This is an OverC container image used for providing system services"
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_EXCLUDE = "busybox* "
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

CUBE_DOM_1_EXTRA_INSTALL ?= ""


IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-util-linux \
                  packagegroup-core-ssh-openssh \
                  packagegroup-container \
                  packagegroup-service-discovery-and-configuration \
                  packagegroup-dom1 \
                  ${CUBE_DOM_1_EXTRA_INSTALL} \
                 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

inherit core-image
inherit builder-base
