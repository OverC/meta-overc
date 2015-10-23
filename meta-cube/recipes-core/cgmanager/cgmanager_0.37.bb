SUMMARY = "CGManager is a central privileged daemon that manages \
cgroups through a simple D-Bus API."

HOMEPAGE = "https://linuxcontainers.org/cgmanager/"
LICENSE = "LGPLv2.1"

inherit autotools pkgconfig

SRC_URI = "https://linuxcontainers.org/downloads/cgmanager/cgmanager-${PV}.tar.gz \
           file://Disable-help2man.patch \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SRC_URI[md5sum] = "764485d42a9197dce0c5a8a29e5e4bc0"
SRC_URI[sha256sum] = "8309e7f2ae5f4a6b52cc2fca62c098b18ecfe90bca2c9c034ba80f68aa427b6e"

# Needs the native nih-dbus-tool
DEPENDS += "libnih libnih-native"
# The test scripts need some extra facilities
RDEPENDS_${PN} += "libnih bash sudo util-linux"

EXTRA_OECONF += "--with-distro=${DISTRO} --with-init-script=\
${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit,', '', d)}\
${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

FILES_${PN} += "/usr/lib/systemd/system/*"

do_install_prepend() {
    # Copy assorted scripts and other control files into the build directory
    # so they get installed.

    if ${@base_contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        cp -a ${S}/config/init/sysvinit/cg* ${WORKDIR}/build/config/init/sysvinit/
    fi
    if ${@base_contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        cp -a ${S}/config/init/systemd/*.service ${WORKDIR}/build/config/init/systemd/
    fi

    cp -a ${S}/tests ${WORKDIR}/build/
}
