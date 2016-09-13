SUMMARY = "Child process reaping utilities for Go"
HOMEPAGE = "https://github.com/hashicorp/go-reap"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

PKG_NAME = "github.com/hashicorp/go-reap"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "2d85522212dcf5a84c6b357094f5c44710441912"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_reap_sysroot_preprocess"

hashicorp_reap_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
