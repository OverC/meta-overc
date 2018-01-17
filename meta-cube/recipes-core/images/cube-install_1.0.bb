SUMMARY = "An image which can be used to run the cube installer scripts."
DESCRIPTION = "Small image which can be used natively or as a cube to\
               run the cube installer. Contains the tools required by\
               the installer scripts."
HOMEPAGE = "http://www.windriver.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_EXCLUDE = "busybox*"

# password-less login
IMAGE_FEATURES += "debug-tweaks"

IMAGE_LINGUAS = ""

IMAGE_FSTYPES ?= "tar.bz2 hddimg"

# Be sure to do this after setting IMAGE_FSTYPES to ensure proper 'live'
# handling. See note in documentation for IMAGE_FSTYPES.
inherit core-image

export IMAGE_BASENAME = "cube-install"

TARGETNAME ?= "cube-install"

# Distro can override the following VIRTUAL-RUNTIME providers:
VIRTUAL-RUNTIME_dev_manager ?= "udev"
VIRTUAL-RUNTIME_login_manager ?= "shadow"
VIRTUAL-RUNTIME_init_manager ?= "systemd"
VIRTUAL-RUNTIME_initscripts ?= ""
VIRTUAL-RUNTIME_keymaps ?= "keymaps"

CUBE_INSTALL_EXTRA_INSTALL ?= "kernel-modules"

# Required package
PACKAGE_INSTALL = " \
    bash \
    base-files \
    base-passwd \
    ${@bb.utils.contains("MACHINE_FEATURES", "efi", "grub-efi efibootmgr", "grub", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "keyboard", "${VIRTUAL-RUNTIME_keymaps}", "", d)} \
    netbase \
    ${VIRTUAL-RUNTIME_login_manager} \
    ${VIRTUAL-RUNTIME_init_manager} \
    ${VIRTUAL-RUNTIME_dev_manager} \
    ${VIRTUAL-RUNTIME_update-alternatives} \
"

# More essential packages
PACKAGE_INSTALL += " \
    shadow \
    "

PACKAGE_INSTALL += " \
    ${CUBE_INSTALL_EXTRA_INSTALL} \
    nfs-utils-client \
    sed \
    vim \
    dhcp-client \
    grep \
    findutils \
    iproute2 \
    gawk \
    iputils \
    dropbear \
    "

# Required by the overc-installer scripts
PACKAGE_INSTALL += " \
    dosfstools \
    util-linux \
    file \
    which \
    jq \
    e2fsprogs \
    perl \
    tar \
    coreutils \
    bzip2 \
    python \
    btrfs-tools \
    "

# Required by cubeit
PACKAGE_INSTALL += " \
    dialog \
    parted \
    procps \
    "

USE_DEVFS = "0"
