SUMMARY = "An immutable radix tree implementation in Golang"
HOMEPAGE = "https://github.com/hashicorp/go-immutable-radix"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea"

PKG_NAME = "github.com/hashicorp/go-immutable-radix"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "8e8ed81f8f0bf1bdd829593fdd5c29922c1ea990"

S = "${WORKDIR}/git"

DEPENDS += " golang-lru"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "hashicorp_radix_sysroot_preprocess"

hashicorp_radix_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a --no-preserve=ownership ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
