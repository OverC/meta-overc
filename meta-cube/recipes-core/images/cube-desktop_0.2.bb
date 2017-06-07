SUMMARY = "A container image for domain E which is used as a graphical interface for users"
DESCRIPTION = "Launched from the essential image, this is a container image \
               which provides a graphical ui. This will be the point of \
               interaction for most users. \
              "
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_FEATURES += "package-management doc-pkgs x11-base"
IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

PACKAGE_EXCLUDE = "busybox*"
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

CUBE_DESKTOP_EXTRA_INSTALL ?= ""

IMAGE_INSTALL += "packagegroup-core-boot \
                        packagegroup-dom0 \
                        packagegroup-util-linux \
                        packagegroup-core-ssh-openssh \
                        packagegroup-core-full-cmdline \
                        packagegroup-builder \
                        packagegroup-xfce \
                        packagegroup-container \
                        packagegroup-networkmanager \
                        packagegroup-audio \
                        ntp \
                        ntpdate \
                        ntp-utils \
                        ${CUBE_DESKTOP_EXTRA_INSTALL} \
                       "

# temp. rpm bug workaround
IMAGE_INSTALL += " dhcp-libs"

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

