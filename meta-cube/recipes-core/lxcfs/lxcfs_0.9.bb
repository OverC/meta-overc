SUMMARY = "LXCFS is a userspace filesystem created to avoid kernel limitations"
LICENSE = "Apache-2.0"

inherit autotools pkgconfig

SRC_URI = "https://linuxcontainers.org/downloads/lxcfs/lxcfs-${PV}.tar.gz \
           file://Disable-help2man.patch \
          "
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI[md5sum] = "455999f60080322852264cfb7d6b11b7"
SRC_URI[sha256sum] = "e58cbb61cbb81498c1962cfba4709c3f4900d0e94789579651f0b95fbc0b19c6"

DEPENDS += "libnih cgmanager fuse"
RDEPENDS_${PN} += "libnih cgmanager fuse"

FILES_${PN} += "${datadir}/lxc/config/common.conf.d/*"
