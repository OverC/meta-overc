SUMMARY = "Column formatted output library for Go"
HOMEPAGE = "https://github.com/ryanuber/columnize"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b1989be3dc78e86f2c54cf3b03db7c9"

PKG_NAME = "github.com/ryanuber/columnize"
SRC_URI = "git://${PKG_NAME}.git"
SRCREV = "44cb4788b2ec3c3d158dd3d1b50aba7d66f4b59a"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}/local/go/src/${PKG_NAME}
    cp -r --preserve=timestamp,mode ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "columnize_sysroot_preprocess"

columnize_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -a ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
