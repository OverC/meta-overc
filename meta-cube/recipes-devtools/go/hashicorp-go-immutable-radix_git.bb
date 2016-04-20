SUMMARY = "An immutable radix tree implementation in Golang"
HOMEPAGE = "https://github.com/hashicorp/go-immutable-radix"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PKG_NAME = "github.com/hashicorp/go-immutable-radix"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "8e8ed81f8f0bf1bdd829593fdd5c29922c1ea990"

S = "${WORKDIR}/git"

DEPENDS += " golang-lru"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_radix_sysroot_preprocess"

hashicorp_radix_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
