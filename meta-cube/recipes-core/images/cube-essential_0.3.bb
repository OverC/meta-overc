SUMMARY = "A minimal native host image for OverC"
DESCRIPTION = "Small native host image for OverC capable of launching \
               the domain0 container."
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# we don't want this to be a container image even
# configure with --enable-container=yes
USE_DEPMOD = "1"
CUBE_ESSENTIAL_EXTRA_INSTALL ?= "kernel-modules"

PACKAGE_EXCLUDE = "busybox*"
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

OVERC_VMSEP_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','vm-sep','packagegroup-vm-sep','',d)}"

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-essential \
                  packagegroup-util-linux \
		  overc-conftools \
		  rndmac \
		  screen-getty \
		  iw \
		  essential-init \
		  pflask \
		  oci-systemd-hook \
                  jq \
                  ${CUBE_ESSENTIAL_EXTRA_INSTALL} \
                  ${OVERC_VMSEP_PACKAGES} \
		  parted \
		  kernel-image \
                 "

IMAGE_FEATURES += "package-management"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

TARGETNAME ?= "cube-essential"

# do not install recommendations
NO_RECOMMENDATIONS = "1"

INITRD = "True"
INITRAMFS_IMAGE = "cube-builder-initramfs"
# We want it separate, and not bundled with the kernel by default.
INITRAMFS_IMAGE_BUNDLE ?= ""

IMAGE_FEATURES += '${@bb.utils.contains("OVERC_ESSENTIAL_MODE", "read-only", "read-only-rootfs", "",d)}'

inherit core-image
inherit builder-base

#
# Sets up a basic networking configuration which consists of a veth
# pair and routing to use the network prime. Usually the veth peer
# will be moved to the cube-vrf and 'plugged' into the cube lan
# bridge but this behavior will depend on the existence of the
# cube-vrf and the network hooks deployed.
#
ROOTFS_POSTPROCESS_COMMAND += "setup_essential_networking; "

setup_essential_networking () {
    # Configure networkd to create a veth pair
    cat > ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.netdev << EOF
[NetDev]
Name=essential
Kind=veth

[Peer]
Name=essential-peer
EOF
    chmod 644 ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.netdev
    chown root.root ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.netdev

    # Set network configuration for the 'internal' end of the veth pair
    cat > ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.network << EOF
[Match]
Name=essential

[Network]
Address=192.168.42.2/24
Gateway=192.168.42.1
DNS=192.168.42.1
EOF
    chmod 644 ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.network
    chown root.root ${IMAGE_ROOTFS}/etc/systemd/network/25-veth.network

    # Setup resolv.conf (removing systemd's default link)
    rm ${IMAGE_ROOTFS}/etc/resolv.conf
    echo "nameserver 192.168.42.1" > ${IMAGE_ROOTFS}/etc/resolv.conf

    # Add ourselves to the /etc/hosts file
    echo "192.168.42.2    cube-essential" >> ${IMAGE_ROOTFS}/etc/hosts
}


ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", "read_only_essential; ", "", d)}'

read_only_essential () {
    if [ -e ${IMAGE_ROOTFS}/etc/hosts ]; then
        mv ${IMAGE_ROOTFS}/etc/hosts ${IMAGE_ROOTFS}/etc/hosts0
    fi
    ln -s ../run/systemd/resolve/hosts ${IMAGE_ROOTFS}/etc/hosts

    if [ -e ${IMAGE_ROOTFS}/etc/dnsmasq.conf ]; then
        mv ${IMAGE_ROOTFS}/etc/dnsmasq.conf ${IMAGE_ROOTFS}/etc/dnsmasq.conf0
    fi
    ln -s ../run/systemd/resolve/dnsmasq.conf ${IMAGE_ROOTFS}/etc/dnsmasq.conf

    if [ -e ${IMAGE_ROOTFS}/etc/resolv.conf ]; then
        mv ${IMAGE_ROOTFS}/etc/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf0
    fi
    ln -s ../run/systemd/resolve/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf

    if [ -e ${IMAGE_ROOTFS}/etc/system-id ]; then
        rm ${IMAGE_ROOTFS}/etc/system-id -f
    fi
    ln -s ../run/systemd/resolve/system-id ${IMAGE_ROOTFS}/etc/system-id
    if [ -e ${IMAGE_ROOTFS}/etc/systemd/network/20-wired.network ]; then
	rm -f ${IMAGE_ROOTFS}/etc/systemd/network/20-wired.network
    fi
    ln -s ../run/systemd/resolve/localtime ${IMAGE_ROOTFS}/etc/localtime
    ln -s ../run/systemd/resolve/timezone ${IMAGE_ROOTFS}/etc/timezone

    mkdir -p ${IMAGE_ROOTFS}/etc/profile.d
    cat > ${IMAGE_ROOTFS}/etc/profile.d/essential_rw.sh << END
rw_test=\$(mount |grep " / "|grep rw)

if [ -z "\$rw_test" ]; then
    if read -t 5 -p "Essential is read-only, if you need to login as read-write, please enter \"yes\":" rw_allow; then
        if [ "\$rw_allow" == "yes" ]; then
            mount / -o remount,rw
        fi
    fi
else
    if read -t 5 -p "Essential is read-write, if you need to login as read-only, please enter \"yes\":" rw_allow; then
        if [ "\$rw_allow" == "yes" ]; then
            umount /
        fi
    fi
fi
echo
rw_test=\$(mount |grep " / "|grep rw)

if [ ! -z "\$rw_test" ]; then
    echo "Warning, Essential rootfs is in read-write, all modification on essential will be recorded."
    echo "To return read-only, please issue \"umount /\" or reboot system."
else
    echo "Essential is in read-only."
fi
END
}
