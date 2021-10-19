build setup and layer fetching
------------------------------

 # Set OVERCDIR to the desired location
 % OVERCDIR = ~/overc
 % mkdir ${OVERCDIR}
 % cd ${OVERCDIR}

 % git clone git://github.com/OverC/overc-installer.git -b master
 % cd overc-installer
 % ./lib/github-fetcher.py -b master OverC meta-overc meta-cube/recipes-support/overc-utils/source/cube-cfg sbin/
 % ./lib/github-fetcher.py -b master OverC meta-overc meta-cube/recipes-support/overc-utils/source/cube-ctl sbin/
 % chmod +x sbin/cube-ctl
 % chmod +x sbin/cube-cfg

 # NOTE: you must have root access, and btrfs-tools and 'jq' installed on the machine
 #       that will run the installer.
 # sudo apt install -y btrfs-tools jq
 
 % cd ${OVERCDIR}
 % git clone git://git.yoctoproject.org/poky
 % git clone git://git.openembedded.org/meta-openembedded
 % git clone git://github.com/OverC/meta-overc.git
 % git clone git://git.yoctoproject.org/meta-virtualization
 % git clone git://git.yoctoproject.org/meta-cloud-services
 % git clone git://git.yoctoproject.org/meta-security
 % git clone git://git.yoctoproject.org/meta-selinux

% source poky/oe-init-build-env build

configuration
-------------

layers:

 % bitbake-layers add-layer ../meta-openembedded/meta-oe/
 % bitbake-layers add-layer ../meta-openembedded/meta-python
 % bitbake-layers add-layer ../meta-openembedded/meta-networking/
 % bitbake-layers add-layer ../meta-openembedded/meta-filesystems/
 % bitbake-layers add-layer ../meta-openembedded/meta-gnome/
 % bitbake-layers add-layer ../meta-openembedded/meta-xfce
 % bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
 % bitbake-layers add-layer ../meta-openembedded/meta-perl
 % bitbake-layers add-layer ../meta-overc/
 % bitbake-layers add-layer ../meta-overc/meta-cube/
 % bitbake-layers add-layer ../meta-virtualization/
 % bitbake-layers add-layer ../meta-cloud-services/
 % bitbake-layers add-layer ../meta-security
 % bitbake-layers add-layer ../meta-selinux

local.conf:
--cut--
cat << EOF >>  conf/local.conf

#
# OverC updates
#

# set the machine
MACHINE = "genericx86-64"
KMACHINE:genericx86-64 = "common-pc-64"

# disto: replace the poky distro with:
DISTRO = "overc"

# build and kernel options
IMAGE_FSTYPES:append = " tar.bz2"
NOISO = "1"
IMAGE_FSTYPES:remove = "hddimg wic wic.bmap"
OVERC_ESSENTIAL_MODE = "read-write"
PREFERRED_PROVIDER_virtual/kernel = "linux-yocto"

# if you are building and booting on an non-efi machine (i.e. like qemu)
MACHINE_FEATURES:remove = "efi"
EOF
--cut--

build
-----

 % bitbake cube-essential
 % bitbake cube-dom0
 % bitbake cube-server
 % bitbake cube-vrf
 % bitbake cube-builder-initramfs
or
 % bitbake cube-essential cube-dom0 cube-server cube-vrf cube-builder-initramfs

installer
---------

 # create the following overc configuration file for a live image that has
 # cube-dom0 and cube-server as payload containers.

 % mkdir ~/.overc/
 % cd ${OVERCDIR}/overc-installer
 % cp config/config-usb.sh.sample ~/.overc/config-usb.sh
 % cp config/config-usb-cube.sh.sample ~/.overc/config-live-dom0-server.sh
 % sed 's/[ \t]*$//' <<'ENDPATCH' > >(patch -p1 --ignore-whitespace ~/.overc/config-live-dom0-server.sh)
--- config-live-dom0-server.sh        2019-09-13 20:15:11.792212539 -0400
+++ config-live-dom0-server.sh.good   2019-09-13 20:14:51.603190437 -0400
@@ -14,11 +14,12 @@
 #   console (container gets a virtual console)
 #   hardconsole (container gets a physical console)
 #   type=<system> (is the container privileged/system ?)
-HDINSTALL_CONTAINERS="${ARTIFACTS_DIR}/cube-dom0-genericx86-64.tar.bz2:vty=2:mergepath=/usr,essential \
+HDINSTALL_CONTAINERS="${ARTIFACTS_DIR}/cube-dom0-genericx86-64.tar.bz2:vty=2:console:net=1 \
                       ${ARTIFACTS_DIR}/cube-vrf-genericx86-64.tar.bz2:net=vrf:app=/usr/bin/docker-init,/sbin/vrf-init \
-                      ${ARTIFACTS_DIR}/cube-desktop-genericx86-64.tar.bz2:vty=3:net=1:mergepath=/usr,essential,dom0 \
                       ${ARTIFACTS_DIR}/cube-server-genericx86-64.tar.bz2:subuid=800000"

+SCREEN_GETTY_CONSOLE=ttyS0,115200
+
 NETWORK_DEVICE="enp0s3"

 ## Uncomment to use GPT
ENDPATCH

 # create the live image
 % sudo ./sbin/cubeit --force  --config config-live-dom0-server.sh --artifacts $(pwd)/../build/tmp/deploy/images/genericx86-64 /tmp/overc-test.img
 
run it!: qemu
--------------

 % sudo qemu-system-x86_64 -enable-kvm -m 2048 \
        -curses -vnc :3 -serial mon:stdio \
        -boot a -drive file=/tmp/overc-test.img,media=disk,if=virtio,index=0 \
        -device virtio-net-pci,id=net0,netdev=hostnet0 \
        -netdev user,id=hostnet0 -device virtio-rng-pci

 # you should see the cube-dom0 login prompt.
 # ctrl-w 0 switches the screen tty mux to cube-essential, ctrl-w 1 to get back
