# Copyright (C) 2015 Wind River Systems
#
# This expands dom0 with extra facilities to keep it looking much
# like previous incarnations.  Remove this bbappend if you want dom0
# to just be a container control mechanism, with the extra facilities
# residing in other domain containers.

include pod-dom0.bb

IMAGE_INSTALL_append = "${DOM0_MAIN_PKGS} \
                        packagegroup-core-full-cmdline \
                        packagegroup-builder \
                        packagegroup-xfce \
                        ntp \
                        ntpdate \
                        ntp-utils \
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

IMAGE_FEATURES += "x11-base"
