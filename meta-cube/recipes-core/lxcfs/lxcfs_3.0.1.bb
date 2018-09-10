SUMMARY = "LXCFS is a userspace filesystem created to avoid kernel limitations"
LICENSE = "Apache-2.0"

inherit autotools pkgconfig systemd

SRC_URI = " \
    https://linuxcontainers.org/downloads/lxcfs/lxcfs-${PV}.tar.gz \
    file://systemd-allow-for-distinct-build-directory.patch \
    file://systemd-ensure-var-lib-lxcfs-exists.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI[md5sum] = "fa49872fc45846125455199a2cce18f1"
SRC_URI[sha256sum] = "016c317f13392bebccba338511f537332fb2fdbaf62a5f6d77307b38a348f41f"

DEPENDS += "fuse"
RDEPENDS_${PN} += "fuse"

FILES_${PN} += "${datadir}/lxc/config/common.conf.d/*"

CACHED_CONFIGUREVARS += "ac_cv_path_HELP2MAN='false // No help2man //'"
EXTRA_OECONF += "--with-distro=unknown --with-init-script=${VIRTUAL-RUNTIME_init_manager}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "lxcfs.service"
