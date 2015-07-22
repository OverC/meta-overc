SUMMARY = "A go based metrics library"
HOMEPAGE = "https://github.com/armon/go-metrics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2d77030c0183e3d1e66d26dc1f243be"

PKG_NAME = "github.com/armon/go-metrics"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "b2d95e5291cdbc26997d1301a5e467ecbb240e25"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_metrics_sysroot_preprocess"

go_metrics_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
