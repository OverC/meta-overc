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
    packagegroup-builder-host-tools \
    "

RDEPENDS_packagegroup-builder = "\
    packagegroup-builder-debug \
    packagegroup-builder-sdk \
    packagegroup-builder-extended \
    packagegroup-builder-graphics \
    packagegroup-builder-host-tools \
    "

RDEPENDS_packagegroup-builder-host-tools = "\
    dhcp-client \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    hdparm \
    iptables \
    lsb \
    xdg-utils \
    mc \
    mc-fish \
    mc-helpers \
    mc-helpers-perl \
    mc-helpers-python \
    leafpad \
    pcmanfm \
    parted \
    pseudo \
    screen \
    vte \
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
    eglibc-utils \
    eglibc-gconv-ibm850 \
    file \
    findutils \
    g++ \
    g++-symlinks \
    gcc \
    gcc-symlinks \
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
    mktemp \
    perl-module-re \
    perl-module-text-wrap \
    pkgconfig \
    quilt \
    sed \
    vim"

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
    diffstat \
    diffutils \
    elfutils \
    expat \
    gamin \
    gawk \
    gdbm \
    gettext \
    gettext-runtime \
    git \
    git-manpages-doc \
    git-perltools \
    grep \
    groff \
    grub \
    gzip \
    settings-daemon \
    hicolor-icon-theme \
    inetutils \
    sato-icon-theme \
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
    python-compile \
    python-compiler \
    python-compression \
    python-core \
    python-curses \
    python-datetime \
    python-difflib \
    python-distutils \
    python-elementtree \
    python-email \
    python-fcntl \
    python-git \
    python-json \
    python-logging \
    python-misc \
    python-mmap \
    python-multiprocessing \
    python-netclient \
    python-netserver \
    python-pickle \
    python-pkgutil \
    python-pprint \
    python-re \
    python-rpm \
    python-shell \
    python-sqlite3 \
    python-subprocess \
    python-textutils \
    python-unittest \
    python-unixadmin \
    python-xmlrpc \
    quota \
    readline \
    rpm \
    setserial \
    socat \
    sudo \
    sysstat \
    tar \
    tcl \
    texi2html \
    texinfo \
    traceroute \
    unzip \
    usbutils \
    watchdog \
    wget \
    which \
    xinetd \
    zip \
    zlib \
    xz \
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
