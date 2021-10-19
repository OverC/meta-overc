#
# Copyright (C) 2015 Wind River
#

OVERC_COMMON_DISK = " \
     btrfs-tools \
     e2fsprogs-tune2fs \
     dosfstools \
     hdparm \
     mdadm \
     parted \
    "

OVERC_DOM0_DISK = " \
     btrfs-tools \
     e2fsprogs-tune2fs \
     dosfstools \
    "

OVERC_COMMON_EXTENDED_ARCH ?= ""
OVERC_COMMON_EXTENDED_ARCH_i586 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"
OVERC_COMMON_EXTENDED_ARCH:x86-64 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"

OVERC_COMMON_EXTENDED = " \
     bash-completion \
     cpio \
     dmidecode \
     dtach \
     ed \
     fbset \
     findutils \
     grep \
     gzip \
     iso-codes \
     kernel-modules \
     less \
     ncurses-terminfo \
     pciutils \
     screen \
     tar \
     unzip \
     vim \
     which \
     zile \
     zip \
     gawk \
     bzip2 \
     procps \
     util-linux \
     kmod \
     sed \
     coreutils \
     e2fsprogs \
     kbd \
     ${OVERC_COMMON_EXTENDED_ARCH} \
    "

OVERC_DOM0_EXTENDED = " \
     cpio \
     dmidecode \
     dtach \
     ed \
     fbset \
     findutils \
     grep \
     gzip \
     less \
     ncurses-terminfo \
     pciutils \
     screen \
     tar \
     unzip \
     vim \
     which \
     zip \
     gawk \
     bzip2 \
     procps \
     util-linux \
     kmod \
     sed \
     coreutils \
     e2fsprogs \
     ${OVERC_COMMON_EXTENDED_ARCH} \
    "

OVERC_SYSTEMD_EXTRAS = "systemd-extra-utils \
                        systemd-vconsole-setup \
                        systemd-serialgetty \
                        systemd-compat-units \
                        udev-hwdb \
                        os-release"

OVERC_COMMON_EXTENDED += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${OVERC_SYSTEMD_EXTRAS}','',d)}"

OVERC_EXTRA_EXTENDED = "\
     cronie \
     flac \
     formfactor \
     fuser \
     pm-utils \
     polkit \
     sysstat \
     tzdata \
     groff \
    "

OVERC_COMMON_NETWORKING = "\
     dhcp-client \
     ifupdown \
     iproute2 \
     iputils \
     iptables \
     iptables-modules \
     net-tools \
     netcat \
     openssh-ssh \
     openvswitch \
    "

OVERC_DOM0_NETWORKING = "\
     iproute2 \
     iputils \
     iptables \
     iptables-modules \
     net-tools \
     netcat \
     openssh-ssh \
    "

OVERC_EXTRA_NETWORKING = "\
     avahi-daemon \
     avahi-utils \
     ca-certificates \
     ethtool \
     tcp-wrappers \
     tcpdump \
     traceroute \
     wget \
    "

OVERC_DOM0_EXTRA_NETWORKING = "\
     wget \
    "

OVERC_COMMON_FS = " \
     quota \
     rsync \
     sysfsutils \
    "

OVERC_DOM0_FS = " \
     sysfsutils \
    "

OVERC_COMMON_DOC = " \
     man \
     man-pages \
    "

OVERC_COMMON_PERL = " \
     perl-misc \
     perl-pod \
    "

OVERC_COMMON_PYTHON = " \
     python3-misc \
     python3-pip \
    "

OVERC_COMMON_TOOLS = "\
     killall \
     lsof \
     patch \
     pstree \
     setserial \
     socat \
     strace \
     sudo \
     time \
     overc-utils \
    "
