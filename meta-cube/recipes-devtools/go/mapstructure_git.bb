SUMMARY = "Go library for decoding generic map values to structures"
HOMEPAGE = "https://github.com/mitchellh/mapstructure"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd"

PKG_NAME = "github.com/mitchellh/mapstructure"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "2caf8efc93669b6c43e0441cdc6aed17546c96f3"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "mapstructure_sysroot_preprocess"

mapstructure_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
