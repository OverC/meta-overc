SUMMARY = "An image creating a file system for a build server itself"
DESCRIPTION = "An image capable of building the system that you can deploy on a server."
HOMEPAGE = "http://www.yoctoproject.org/documentation/meta-builder"

# fixme ; point at layer copy someday.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# fixme: core-boot has a hard dep on boogerbox
IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-openssh packagegroup-core-full-cmdline packagegroup-builder"

# doc-pkgs would be nice but it blows up in a nasty way.
#IMAGE_FEATURES += "package-management doc-pkgs"
IMAGE_FEATURES += "package-management"

#DEPENDS = "zip-native"
IMAGE_FSTYPES = "tar.bz2"

inherit core-image

#SRCREV ?= "68ef727cdcef439e9bfc57996f3cebfc0e07789e"
#SRC_URI = "git://git.yoctoproject.org/poky \
#          "
