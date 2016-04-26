SUMMARY = "Serve embedded files from jteeuwen/go-bindata with net/http."
HOMEPAGE = "https://github.com/elazarl/go-bindata-assetfs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=722abb44e97dc8f098516e09e5564a6a"

PKG_NAME = "github.com/elazarl/go-bindata-assetfs"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "57eb5e1fc594ad4b0b1dbea7b286d299e0cb43c2"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_bindata_sysroot_preprocess"

go_bindata_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
