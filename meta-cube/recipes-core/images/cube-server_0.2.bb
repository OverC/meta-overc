SUMMARY = "A container image for domain server which is used as a headless, full development server"
DESCRIPTION = "Launched from the essential image, this is a container image \
               which provides a headless install capable of being used as a server \
               and as a development platform. \
              "
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"


CUBE_SERVER_EXTRA_INSTALL ?= ""

IMAGE_FEATURES += "package-management doc-pkgs"
IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

PACKAGE_EXCLUDE = "busybox*"
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-core-ssh-openssh \
                  packagegroup-core-full-cmdline \
                  packagegroup-util-linux \
                  packagegroup-builder \
                  packagegroup-dom0 \
                  packagegroup-container \
                  ${CUBE_SERVER_EXTRA_INSTALL} \
                  "

TARGETNAME ?= "cube-server"

inherit core-image
inherit builder-base

