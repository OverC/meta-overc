SUMMARY = "Library for implementing command-line interfaces in Go"
HOMEPAGE = "https://github.com/mitchellh/cli"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b278a92d2c1509760384428817710378"

DEPENDS = "crypto"

PKG_NAME = "github.com/mitchellh/cli"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "76e2780bc4f71797e1ce037ac0b43b7c99b31749"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "cli_sysroot_preprocess"

cli_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
