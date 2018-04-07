SUMMARY = "A container image for domain E which is used as a graphical interface for users"
DESCRIPTION = "Launched from the essential image, this is a container image \
               which provides a graphical ui. This will be the point of \
               interaction for most users. \
              "
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_FEATURES += "package-management x11-base"
IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

require recipes-core/packagegroups/overc-common-pkgdefs.inc

PACKAGE_EXCLUDE = "busybox*"
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

CUBE_DESKTOP_EXTRA_INSTALL ?= ""

RDEPENDS_packagegroup-dom0_remove = "linux-firmware"

IMAGE_INSTALL += "packagegroup-core-boot \
                        packagegroup-util-linux \
                        packagegroup-core-ssh-openssh \
                        packagegroup-core-full-cmdline \
                        packagegroup-xfce \
                        packagegroup-container \
                        packagegroup-networkmanager \
                        packagegroup-audio \
                        ${OVERC_COMMON_TOOLS} \
                        ${OVERC_COMMON_EXTENDED} \
                        ntp \
                        ntpdate \
                        ntp-utils \
                        ${CUBE_DESKTOP_EXTRA_INSTALL} \
                       "

XSERVER_append = "xserver-xorg \
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
                  xf86-video-vmware \
                 "

ALTERNATIVE_PRIORITY_xfce4-session[x-session-manager] = "60"

TARGETNAME ?= "cube-desktop"

inherit core-image
inherit builder-base

# Override the cube configuration of networkd
ROOTFS_POSTPROCESS_COMMAND_remove = "systemd_openvswitch_network;"
