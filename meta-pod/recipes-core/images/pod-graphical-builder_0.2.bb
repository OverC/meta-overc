SUMMARY = "An image creating a file system for a build server itself"
DESCRIPTION = "An image capable of building the system that you can deploy on a server."
HOMEPAGE = "http://www.windriver.com"

# fixme ; point at layer copy someday.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

POD_BUILDER_EXTRA_INSTALL ?= ""

# fixme: core-boot has a hard dep on boogerbox
IMAGE_INSTALL += "packagegroup-core-boot \
		  packagegroup-core-ssh-openssh \
		  packagegroup-core-full-cmdline \
		  packagegroup-util-linux \
		  packagegroup-builder \
		  packagegroup-container \
		  packagegroup-graphical-builder \
		  ${POD_BUILDER_EXTRA_INSTALL} \
		 "

XSERVER_append = " xserver-xorg \
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

IMAGE_FSTYPES = "tar.bz2"

inherit core-image
inherit builder-base
