SUMMARY = "Child process reaping utilities for Go"
HOMEPAGE = "https://github.com/hashicorp/go-reap"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PKG_NAME = "github.com/hashicorp/go-reap"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "2d85522212dcf5a84c6b357094f5c44710441912"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_reap_sysroot_preprocess"

hashicorp_reap_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
