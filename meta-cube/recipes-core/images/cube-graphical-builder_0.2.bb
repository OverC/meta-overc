SUMMARY = "An image creating a file system for a build server itself"
DESCRIPTION = "An image capable of building the system that you can deploy on a server."
HOMEPAGE = "http://www.windriver.com"

# fixme ; point at layer copy someday.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

CUBE_BUILDER_EXTRA_INSTALL ?= ""

# fixme: core-boot has a hard dep on boogerbox
IMAGE_INSTALL += "packagegroup-core-boot \
		  packagegroup-core-ssh-openssh \
		  packagegroup-core-full-cmdline \
		  packagegroup-util-linux \
		  packagegroup-builder \
		  packagegroup-graphical-builder \
		  ${CUBE_BUILDER_EXTRA_INSTALL} \
		 "

XSERVER:append = " xserver-xorg \
           xserver-xorg-extension-dri \
           xserver-xorg-extension-dri2 \
           xserver-xorg-extension-glx \
           xserver-xorg-extension-extmod \
           xserver-xorg-extension-dbe \
           xserver-xorg-module-libint10 \
           xf86-input-evdev \
           xf86-input-keyboard \
           xf86-input-mouse \
           xf86-input-synaptics \
           xf86-input-vmmouse \
           xf86-video-ati \
           xf86-video-fbdev \
           xf86-video-intel \
           xf86-video-mga \
           xf86-video-modesetting \
           xf86-video-nouveau \
           xf86-video-vesa \
           xf86-video-vmware"

ALTERNATIVE_PRIORITY_xfce4-session[x-session-manager] = "60"

IMAGE_FEATURES += "x11-base"
IMAGE_FEATURES += "package-management doc-pkgs"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES:remove = "live"

inherit core-image
inherit builder-base
