SUMMARY = "An image creating a file system for a build server itself"
DESCRIPTION = "An image capable of building the system that you can deploy on a server."
HOMEPAGE = "http://www.yoctoproject.org/documentation/meta-builder"

# fixme ; point at layer copy someday.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# fixme: core-boot has a hard dep on boogerbox
IMAGE_INSTALL += "packagegroup-core-boot \
		  packagegroup-core-ssh-openssh \
		  packagegroup-core-full-cmdline \
		  packagegroup-util-linux \
		  packagegroup-builder \
		  ${YOCTO_BUILDER_EXTRA_INSTALL} \
		 "

IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES = "tar.bz2"

inherit core-image
inherit builder-base
