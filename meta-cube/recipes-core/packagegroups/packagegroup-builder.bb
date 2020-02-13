#
# Original packagegroup-builder Copyright (C) 2010 Intel Corporation
#
# builder variant Copyright (C) 2014 Wind River
#

SUMMARY = "Self-hosting builder"
DESCRIPTION = "Packages required to run the build system w/o gfx"
PR = "r1"
LICENSE = "MIT"

# grr - packagegroup is special and wants to be allarch, which
# doesn't work so well if we want to filter grub for MACHINE_FEATURES, etc.
PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PACKAGES = "\
    packagegroup-builder \
    packagegroup-builder-debug \
    packagegroup-builder-sdk \
    packagegroup-builder-extended \
    packagegroup-builder-graphics \
    packagegroup-builder-networking \
    packagegroup-builder-host-tools \
    "

RDEPENDS_packagegroup-builder = "\
    packagegroup-builder-debug \
    packagegroup-builder-sdk \
    packagegroup-builder-extended \
    packagegroup-builder-graphics \
    packagegroup-builder-networking \
    packagegroup-builder-host-tools \
    "

RDEPENDS_packagegroup-builder-host-tools = "\
    btrfs-tools \
    debianutils \
    dhcp-client \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    hdparm \
    iptables \
    iptables-modules \
    parted \
    pseudo \
    screen \
    sed \
    vim \
    "

RRECOMMENDS_packagegroup-builder-host-tools = "\
    kernel-module-tun \
    kernel-module-iptable-raw \
    kernel-module-iptable-nat \
    kernel-module-iptable-mangle \
    kernel-module-iptable-filter \
    "

# eglibc-utils: for rpcgen
RDEPENDS_packagegroup-builder-sdk = "\
    autoconf \
    automake \
    binutils \
    binutils-symlinks \
    ccache \
    coreutils \
    cpp \
    cpp-symlinks \
    diffstat \
    diffutils \
    eglibc-utils \
    file \
    findutils \
    g++ \
    g++-symlinks \
    gcc \
    gcc-symlinks \
    git \
    git-bash-completion \
    git-perltools \
    intltool \
    ldd \
    less \
    libstdc++ \
    libstdc++-dev \
    libtool \
    make \
    perl-module-re \
    perl-module-text-wrap \
    pkgconfig \
    quilt \
    texinfo \
    zile"

RDEPENDS_packagegroup-builder-debug = " \
    gdb \
    gdbserver \
    rsync \
    strace \
    "


RDEPENDS_packagegroup-builder-extended_ARCH ?= ""
RDEPENDS_packagegroup-builder-extended_ARCH_i586 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"
RDEPENDS_packagegroup-builder-extended_ARCH_x86-64 += "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'grub-efi', 'grub', d)}"

RDEPENDS_packagegroup-builder-extended = "\
    bash-completion \
    bzip2 \
    chkconfig \
    chrpath \
    cpio \
    curl \
    dmidecode \
    dpkg \
    elfutils \
    expat \
    gawk \
    gdbm \
    gettext \
    gettext-runtime \
    grep \
    groff \
    gzip \
    ifupdown \
    inetutils \
    kernel-image \
    kernel-modules \
    libaio \
    libusb1 \
    libxml2 \
    lrzsz \
    lsof \
    lzo \
    man \
    man-pages \
    mdadm \
    minicom \
    mtools \
    ncurses \
    ncurses-terminfo-base \
    netcat \
    nfs-utils \
    nfs-utils-client \
    openssl \
    openssh-sftp-server \
    opkg \
    opkg-utils \
    patch \
    perl \
    perl-dev \
    perl-misc \
    perl-modules \
    perl-pod \
    python3-core \
    python3-modules \
    python3-misc \
    python3-git \
    python3 \
    python3-rpm \
    quota \
    readline \
    rpm \
    setserial \
    smartmontools \
    nanomsg \
    sudo \
    sysstat \
    tar \
    tcl \
    tcpdump \
    traceroute \
    unzip \
    usbutils \
    watchdog \
    wget \
    which \
    wiggle \
    xinetd \
    zip \
    zlib \
    xterm \
    xz \
    ${RDEPENDS_packagegroup-builder-extended_ARCH} \
    "

RDEPENDS_packagegroup-builder-networking = "\
    bind \
    bind-utils \
    mutt \
    msmtp \
    "

RDEPENDS_packagegroup-builder-graphics = "\
    libgl \
    libgl-dev \
    libglu \
    libglu-dev \
    libsdl2 \
    libsdl2-dev \
    libx11-dev \
    "
