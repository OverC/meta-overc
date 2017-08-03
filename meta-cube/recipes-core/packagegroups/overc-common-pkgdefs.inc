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

OVERC_COMMON_EXTENDED_ARCH ?= ""
OVERC_COMMON_EXTENDED_ARCH_i586 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"
OVERC_COMMON_EXTENDED_ARCH_x86-64 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"

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
     dhcp-libs \
     ifupdown \
     inetutils \
     iproute2 \
     iputils \
     iptables \
     net-tools \
     netcat \
     openssh-ssh \
     openvswitch \
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

OVERC_COMMON_FS = " \
     quota \
     rsync \
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
     python-misc \
     python-pip \
    "

OVERC_COMMON_TOOLS = "\
     killall \
     lsof \
     lttng-ust \
     patch \
     pax \
     pseudo \
     pstree \
     setserial \
     socat \
     nanomsg \
     strace \
     sudo \
     tcl \
     time \
     overc-utils \
     nanoio \
    "
