#
# Copyright (C) 2015 Wind River
#

OVERC_COMMON_DISK = " \
     btrfs-tools \
     e2fsprogs-tune2fs \
     hdparm \
     mdadm \
     mktemp \
     parted \
    "

OVERC_COMMON_EXTENDED = " \
     bash-completion \
     cpio \
     dmidecode \
     ed \
     fbset \
     findutils \
     grep \
     groff \
     grub \
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
    "

OVERC_EXTRA_EXTENDED = "\
     cronie \
     flac \
     formfactor \
     fuser \
     pm-utils \
     polkit \
     sysstat \
     tcf-agent \
     tzdata \
    "

OVERC_COMMON_NETWORKING = "\
     dhcp-client \
     ifupdown \
     inetutils \
     iproute2 \
     iputils \
     iptables \
     net-tools \
     netcat \
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
     samba \
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
     strace \
     sudo \
     tcl \
     time \
    "
