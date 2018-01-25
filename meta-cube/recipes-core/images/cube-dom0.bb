SUMMARY = "A container image for domain 0 which is used for container control"
DESCRIPTION = "Launched from the essential image, this is a container image \
               used for controlling other containers in the system. \
              "
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

CUBE_DOM0_EXTRA_INSTALL ?= " "

OVERC_VMSEP_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','vm-sep','packagegroup-runv','',d)}"

PACKAGE_EXCLUDE = "busybox* "
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

# do not install recommendations
NO_RECOMMENDATIONS = "1"

PV = "0.5"

FEATURE_PACKAGES_package-management_append = " cube-update"

DOM0_MAIN_PKGS = " packagegroup-core-boot \
                   packagegroup-dom0 \
                   packagegroup-util-linux \
                   packagegroup-core-ssh-openssh \
                   dom0-init \
                 "

IMAGE_INSTALL += "${DOM0_MAIN_PKGS} \
                  packagegroup-container \
                  packagegroup-oci \
                  packagegroup-service-discovery \
                  cube-cmd-server \
                  overc-device-utils \
                  ${OVERC_VMSEP_PACKAGES} \
                  ${CUBE_DOM0_EXTRA_INSTALL} \
                 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

TARGETNAME ?= "cube-dom0"

inherit core-image
inherit builder-base
