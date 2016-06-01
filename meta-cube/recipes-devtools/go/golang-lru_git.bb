SUMMARY = "A fixed-size thread safe LRU cache in go"
HOMEPAGE = "https://github.com/hashicorp/golang-lru"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f27a50d2e878867827842f2c60e30bfc"

PKG_NAME = "github.com/hashicorp/golang-lru"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "a0d98a5f288019575c6d1f4bb1573fef2d1fcdc4"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "golang_lru_sysroot_preprocess"

golang_lru_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
