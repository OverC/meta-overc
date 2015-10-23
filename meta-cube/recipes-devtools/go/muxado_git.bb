SUMMARY = "Stream multiplexing for Go"
HOMEPAGE = "https://github.com/inconshreveable/muxado"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00408bf9ccd61d1ef4e806388fc389a7"

PKG_NAME = "github.com/inconshreveable/muxado"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "f693c7e88ba316d1a0ae3e205e22a01aa3ec2848"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "muxado_sysroot_preprocess"

muxado_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
