#
# Original packagegroup-builder Copyright (C) 2010 Intel Corporation
#
# builder variant Copyright (C) 2014 Wind River
#

SUMMARY = "Self-hosting builder"
DESCRIPTION = "Packages required to run the build system w/o gfx"
PR = "r1"
LICENSE = "MIT"

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
    lsb \
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
    libssp \
    libssp-dev \
    libssp-staticdev \
    libstdc++ \
    libstdc++-dev \
    libtool \
    make \
    perl-module-re \
    perl-module-text-wrap \
    pkgconfig \
    quilt \
    rpm-build \
    texi2html \
    texinfo \
    zile"

RDEPENDS_packagegroup-builder-debug = " \
    gdb \
    gdbserver \
    rsync \
    strace \
    tcf-agent"


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
    grub \
    gzip \
    hicolor-icon-theme \
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
    neon \
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
    pth \
    python \
    python-compiler \
    python-git \
    python-misc \
    python-modules \
    python-rpm \
    quota \
    readline \
    rpm \
    sato-icon-theme \
    setserial \
    settings-daemon \
    smartmontools \
    socat \
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
    libsdl \
    libsdl-dev \
    libx11-dev \
    python-pygtk \
    gtk-theme-clearlooks \
    "
