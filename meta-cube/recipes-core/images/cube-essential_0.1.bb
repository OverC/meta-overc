SUMMARY = "A minimal native host image for OverC"
DESCRIPTION = "Small native host image for OverC capable of launching \
               the domain0 container."
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"


# we don't want this to be a container image even
# configure with --enable-container=yes
USE_DEPMOD = "1"
CUBE_ESSENTIAL_EXTRA_INSTALL ?= "kernel-modules"

PACKAGE_EXCLUDE = "busybox*"
# Exclude documention packages, which can be installed later
PACKAGE_EXCLUDE_COMPLEMENTARY = "ruby|ruby-shadow|puppet|hiera|facter"

IMAGE_INSTALL += "packagegroup-core-boot \
                  packagegroup-essential \
                  packagegroup-util-linux \
                  packagegroup-container \
		  packagegroup-container-setup \
		  cube-update \
		  overc-conftools \
		  rndmac \
		  screen-getty \
		  iw \
                  ${CUBE_ESSENTIAL_EXTRA_INSTALL} \
                 "

IMAGE_FEATURES += "package-management"

IMAGE_FSTYPES ?= "tar.bz2"
IMAGE_FSTYPES_remove = "live"

TARGETNAME ?= "cube-essential"

INITRD = "True"
INITRAMFS_IMAGE = "cube-builder-initramfs"
# We want it separate, and not bundled with the kernel by default.
INITRAMFS_IMAGE_BUNDLE ?= ""

IMAGE_FEATURES += "read-only-rootfs"

inherit core-image
inherit builder-base

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", "read_only_essential; ", "",d)}'

read_only_essential () {
    echo "none	/etc/openvswitch	tmpfs	defaults	1	1" >> ${IMAGE_ROOTFS}/etc/fstab
    mkdir -p ${IMAGE_ROOTFS}/opt/container
    echo "none	/opt/container	tmpfs	defaults	1	1" >> ${IMAGE_ROOTFS}/etc/fstab
    echo "none	/var/lib/misc	tmpfs	defaults	1	1" >> ${IMAGE_ROOTFS}/etc/fstab
    if [ -e ${IMAGE_ROOTFS}/etc/hosts ]; then
        mv ${IMAGE_ROOTFS}/etc/hosts ${IMAGE_ROOTFS}/etc/hosts0
        ln -s ../run/systemd/resolve/hosts ${IMAGE_ROOTFS}/etc/hosts
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/dnsmasq.conf ]; then
        mv ${IMAGE_ROOTFS}/etc/dnsmasq.conf ${IMAGE_ROOTFS}/etc/dnsmasq.conf0
        ln -s ../run/systemd/resolve/dnsmasq.conf ${IMAGE_ROOTFS}/etc/dnsmasq.conf
    fi
    ln -s ../run/systemd/resolve/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf
    if [ -e ${IMAGE_ROOTFS}/etc/system-id ]; then
        rm ${IMAGE_ROOTFS}/etc/system-id -f
    fi
    ln -s ../run/systemd/resolve/system-id ${IMAGE_ROOTFS}/etc/system-id
    if [ -e ${IMAGE_ROOTFS}/lib/systemd/system/cube-cmd-server.service ]; then
	sed -i '/^\[Service\]/a\\ExecStartPre=/bin/sh -c "mkdir -p  /opt/container/dom0 /opt/container/cube-desktop /opt/container/all /opt/container/local"' ${IMAGE_ROOTFS}/lib/systemd/system/cube-cmd-server.service
    fi
    if [ -e ${IMAGE_ROOTFS}/lib/systemd/system/overc-conftools.service ]; then
	sed -i '/ExecStart/d' ${IMAGE_ROOTFS}/lib/systemd/system/overc-conftools.service
	sed -i '/^\[Service\]/a\\ \
ExecStartPre=/bin/sh -c "cp /etc/hosts0 /run/systemd/resolve/hosts" \
ExecStartPre=/bin/sh -c "cp /etc/dnsmasq.conf0 /run/systemd/resolve/dnsmasq.conf" \
ExecStart=/bin/sh -c "/usr/bin/ansible-playbook /etc/overc-conf/ansible/overc.yml" \
' ${IMAGE_ROOTFS}/lib/systemd/system/overc-conftools.service
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/systemd/network/20-wired.network ]; then
	rm -f ${IMAGE_ROOTFS}/etc/systemd/network/20-wired.network
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/overc-conf/network_prime/25-br-int.network.essential ]; then
	cp ${IMAGE_ROOTFS}/etc/overc-conf/network_prime/25-br-int.network.essential ${IMAGE_ROOTFS}/etc/systemd/network/25-br-int.network
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/overc-conf/ansible/essential.yml ]; then
        sed -i '/Copy networkd systemd configuration.*essential/,/25-br-int.network.essential/d' ${IMAGE_ROOTFS}/etc/overc-conf/ansible/essential.yml
        sed -i '/Remove default configuration.*essential/,/20-wired.network/d' ${IMAGE_ROOTFS}/etc/overc-conf/ansible/essential.yml
        sed -i '/^ *file.*20-wired.network.essential/,/}/d' ${IMAGE_ROOTFS}/etc/overc-conf/ansible/essential.yml
        sed -i 's/\/etc\/resolv.conf/\/run\/systemd\/resolve\/resolv.conf/g' ${IMAGE_ROOTFS}/etc/overc-conf/ansible/essential.yml
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/overc-conf/ansible/post.yml ]; then
        sed -i '/name: disable configure_network_prime/,/replace:.*configure_network_prime/d' ${IMAGE_ROOTFS}/etc/overc-conf/ansible/post.yml
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/overc-conf/network_prime/autonetdev ]; then
        sed -i '/echo lxc.network.type = /i\    sed -i "/lxc.network.type = phys/d" $config\' ${IMAGE_ROOTFS}/etc/overc-conf/network_prime/autonetdev
        sed -i '/echo lxc.network.type = /i\    sed -i "/lxc.network.link/d" $config\' ${IMAGE_ROOTFS}/etc/overc-conf/network_prime/autonetdev
    fi
    if [ -e ${IMAGE_ROOTFS}/etc/ansible/ansible.cfg ]; then
	sed -i '/^\[defaults\]/a\\ \
remote_tmp     = /var/lib/misc \
local_tmp      = /var/lib/misc \
' ${IMAGE_ROOTFS}/etc/ansible/ansible.cfg
    fi
    ln -s ../run/systemd/resolve/localtime ${IMAGE_ROOTFS}/etc/localtime
    ln -s ../run/systemd/resolve/timezone ${IMAGE_ROOTFS}/etc/timezone
    ln -s /run/systemd/resolve/cube-cmd-server.log ${IMAGE_ROOTFS}/var/log/cube-cmd-server.log
}
