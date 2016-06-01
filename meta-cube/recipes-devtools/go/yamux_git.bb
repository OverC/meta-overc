SUMMARY = "Multiplexing library for Golang"
HOMEPAGE = "https://github.com/hashicorp/yamux"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2dd1a9ecf92cd5617f128808f9b85b44"

PKG_NAME = "github.com/hashicorp/yamux"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "c8a8a076e0858546ae6e2ee189d94e78b9b881ce"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "yamux_sysroot_preprocess"

yamux_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
