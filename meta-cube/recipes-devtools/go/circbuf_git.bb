SUMMARY = "Go package for circular buffers"
HOMEPAGE = "https://github.com/armon/circbuf"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2d77030c0183e3d1e66d26dc1f243be"

PKG_NAME = "github.com/armon/circbuf"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "f092b4f207b6e5cce0569056fba9e1a2735cb6cf"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "circbuf_sysroot_preprocess"

circbuf_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
