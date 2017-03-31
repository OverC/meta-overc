build setup and layer fetching
------------------------------

 % mkdir overc
 % cd overc

 % git clone git://github.com/OverC/overc-installer.git -b master-oci
 % cd overc-installer
 % ./lib/github-fetcher.py -b master-oci OverC meta-overc meta-cube/recipes-support/overc-utils/source/cube-cfg sbin/
 % ./lib/github-fetcher.py -b master-oci OverC meta-overc meta-cube/recipes-support/overc-utils/source/cube-ctl sbin/
 % chmod +x sbin/cube-ctl
 % chmod +x sbin/cube-cfg

 # NOTE: you must have root access, and btrfs-tools and 'jq' installed on the machine
 #       that will run the installer.
 
 % git checkout -b master-next origin/master-oci
 % cd ..

 % git clone git://git.yoctoproject.org/poky
 % git clone git://git.openembedded.org/meta-openembedded
 % cd meta-openembedded
 % git checkout -b master-next origin/master-next
 % cd ..
 % git clone git://github.com/OverC/meta-overc.git
 % cd meta-overc
 % git checkout -b master-oci origin/master-oci
 % cd ..
 % git clone git://git.yoctoproject.org/meta-virtualization
 % git clone git://git.yoctoproject.org/meta-security

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
 % bitbake-layers add-layer ../meta-openembedded/meta-ruby/
 % bitbake-layers add-layer ../meta-overc/
 % bitbake-layers add-layer ../meta-overc/meta-cube/
 % bitbake-layers add-layer ../meta-virtualization/
 % bitbake-layers add-layer ../meta-security

local.conf:

 # set the machine
 MACHINE="genericx86-64"
 KMACHINE_genericx86-64 ?= "common-pc-64"

 # disto: replace the poky distro with:
 DISTRO ?= "overc"

 # build and kernel options
 IMAGE_FSTYPES_append = " tar.bz2"
 OVERC_ESSENTIAL_MODE = "read-write"
 PREFERRED_PROVIDER_virtual/kernel = "linux-yocto"

build
-----

 % bitbake cube-essential
 % bitbake cube-dom0
 % bitbake cube-server

installer
---------

 # create the following overc configuration file for a live image that has
 # cube-dom0 and cube-server as payload containers.

 % mkdir ~/.overc/
 % cd ~/overc/overc-installer
 % cp config/config-usb.sh.sample ~/.overc/config-usb.sh

 % cat ~/.overc/config-live-dom0-server.sh

source config-usb.sh

SCREEN_GETTY_CONSOLE=ttyS0,115200
TARGET_DISK_SIZE=10G

HDINSTALL_ROOTFS="${ARTIFACTS_DIR}/cube-essential-genericx86-64.tar.bz2"

HDINSTALL_CONTAINERS="${ARTIFACTS_DIR}/cube-dom0-genericx86-64.tar.bz2:console:vty=2:net=1 \
                      ${ARTIFACTS_DIR}/cube-server-genericx86-64.tar.bz2:vty=4"

NETWORK_DEVICE="enp0s3"
NETWORK_DEVICE_CLASSES="en*"

 # create the live image
 % cd tmp/deploy/images/genericx86-64
 % sudo ~/overc/overc-installer/sbin/cubeit -v -v  --force  --config config-live-dom0-server.sh --artifacts `pwd` /tmp/overc-test.img
 
run it!: qemu
--------------

 % sudo qemu-system-x86_64 -enable-kvm -m 2048 \
        -curses -vnc :3 -serial mon:stdio \
        -boot a -drive file=/tmp/overc-test.img,media=disk,if=virtio,index=0 \
        -device virtio-net-pci,id=net0 \
        -netdev user,id=hostnet0

 # you should see the cube-dom0 login prompt.
 # ctrl-w 0 switches the screen tty mux to cube-essential, ctrl-w 1 to get back
