SUMMARY = "reflectwalk is a Go library for "walking" complex structures, similar to walking a filesystem."
HOMEPAGE = "https://github.com/mitchellh/reflectwalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd"

PKG_NAME = "github.com/mitchellh/reflectwalk"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "eecf4c70c626c7cfbb95c90195bc34d386c74ac6"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "reflectwalk_sysroot_preprocess"

reflectwalk_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
